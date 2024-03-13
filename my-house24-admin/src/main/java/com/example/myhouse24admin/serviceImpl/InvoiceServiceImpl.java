package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.mapper.ApartmentOwnerMapper;
import com.example.myhouse24admin.mapper.InvoiceItemMapper;
import com.example.myhouse24admin.mapper.InvoiceMapper;
import com.example.myhouse24admin.model.invoices.*;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.InvoiceItemRepo;
import com.example.myhouse24admin.repository.InvoiceRepo;
import com.example.myhouse24admin.repository.ServicesRepo;
import com.example.myhouse24admin.service.CashRegisterService;
import com.example.myhouse24admin.service.InvoiceService;
import com.example.myhouse24admin.service.PaymentItemService;
import com.example.myhouse24admin.service.StaffService;
import com.example.myhouse24admin.util.PdfGenerator;
import com.example.myhouse24admin.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.fop.apps.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.example.myhouse24admin.specification.InvoiceItemSpecification.byInvoiceId;
import static com.example.myhouse24admin.specification.InvoiceSpecification.*;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepo invoiceRepo;
    private final InvoiceItemRepo invoiceItemRepo;
    private final ApartmentRepo apartmentRepo;
    private final ServicesRepo servicesRepo;
    private final ApartmentOwnerMapper apartmentOwnerMapper;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceItemMapper invoiceItemMapper;
    private final CashRegisterService cashRegisterService;
    private final StaffService staffService;
    private final PaymentItemService paymentItemService;

    private final PdfGenerator pdfGenerator;
    private final Logger logger = LogManager.getLogger(InvoiceServiceImpl.class);

    public InvoiceServiceImpl(InvoiceRepo invoiceRepo,
                              InvoiceItemRepo invoiceItemRepo,
                              ApartmentRepo apartmentRepo,
                              ServicesRepo servicesRepo,
                              ApartmentOwnerMapper apartmentOwnerMapper,
                              InvoiceMapper invoiceMapper,
                              InvoiceItemMapper invoiceItemMapper, CashRegisterService cashRegisterService, StaffService staffService, PaymentItemService paymentItemService) {
                              InvoiceItemMapper invoiceItemMapper,
                              PdfGenerator pdfGenerator) {
        this.invoiceRepo = invoiceRepo;
        this.invoiceItemRepo = invoiceItemRepo;
        this.apartmentRepo = apartmentRepo;
        this.servicesRepo = servicesRepo;
        this.apartmentOwnerMapper = apartmentOwnerMapper;
        this.invoiceMapper = invoiceMapper;
        this.invoiceItemMapper = invoiceItemMapper;
        this.cashRegisterService = cashRegisterService;
        this.staffService = staffService;
        this.paymentItemService = paymentItemService;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public String createNumber() {
        logger.info("createNumber - Creating invoice number");
        Optional<Invoice> lastInvoice = invoiceRepo.findLast();
        String number = lastInvoice.map(invoice -> formNumber(invoice.getNumber())).orElse("0000000001");
        logger.info("createNumber - Invoice number was created");
        return number;
    }

    private String formNumber(String number) {
        int numberPart = Integer.parseInt(number);
        return StringUtils.leftPad(Integer.toString(numberPart + 1), 10, "000000000");
    }

    @Override
    public OwnerResponse getOwnerResponse(Long apartmentId) {
        logger.info("getOwnerResponse - Getting owner response by apartment id " + apartmentId);
        Apartment apartment = apartmentRepo.findById(apartmentId).orElseThrow(() -> new EntityNotFoundException("Apartment was not found by id " + apartmentId));
        OwnerResponse ownerResponse = apartmentOwnerMapper.apartmentToOwnerResponse(apartment);
        logger.info("getOwnerResponse - Owner response was got");
        return ownerResponse;
    }

    @Override
    public void createInvoice(InvoiceRequest invoiceRequest) {
        logger.info("createInvoice - Creating new invoice " + invoiceRequest.toString());
        Apartment apartment = apartmentRepo.findById(invoiceRequest.getApartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Apartment was not found by id " + invoiceRequest.getApartmentId()));
        setNewApartmentBalance(apartment, invoiceRequest);
        String number = createNumber();
        Invoice invoice = invoiceMapper.invoiceRequestToInvoice(invoiceRequest,
                apartment, number);
        Invoice savedInvoice = invoiceRepo.save(invoice);
        createCashSheetFromInvoice(savedInvoice);
        saveInvoiceItems(invoiceRequest.getItemRequests(), savedInvoice);
        logger.info("createInvoice - Invoice was created");
    }

    private void createCashSheetFromInvoice(Invoice newInvoice) {
        String nextSheetNumber = cashRegisterService.getNextSheetNumber();
        Staff currentStaff = staffService.getCurrentStaff();
        PaymentItem defaultPaymentItemForInvoices = paymentItemService.getDefaultPaymentItemForInvoices();
        CashSheet cashSheetFromInvoice = invoiceMapper.invoiceToCashSheet(
                nextSheetNumber,
                newInvoice,
                currentStaff,
                defaultPaymentItemForInvoices);
        cashRegisterService.saveCashSheet(cashSheetFromInvoice);
    }

    private void setNewApartmentBalance(Apartment apartment, InvoiceRequest invoiceRequest) {
        BigDecimal remainder = invoiceRequest.getPaid().subtract(invoiceRequest.getTotalPrice());
        BigDecimal newBalance = apartment.getBalance().add(remainder);
        apartment.setBalance(newBalance);
    }

    @Override
    public Page<TableInvoiceResponse> getInvoiceResponsesForTable(Map<String, String> requestMap) {
        logger.info("getInvoiceResponsesForTable - Getting invoice responses for table " + requestMap.toString());
        Pageable pageable = PageRequest.of(Integer.valueOf(requestMap.get("page")), Integer.valueOf(requestMap.get("pageSize")));
        Page<Invoice> invoicePage = getFilteredInvoices(requestMap, pageable);
        List<TableInvoiceResponse> tableInvoiceResponses = new ArrayList<>();
        for (Invoice invoice : invoicePage.getContent()) {
            BigDecimal totalPrice = invoiceItemRepo.getItemsSumByInvoiceId(invoice.getId());
            TableInvoiceResponse tableInvoiceResponse = invoiceMapper.invoiceToTableInvoiceResponse(invoice, totalPrice);
            tableInvoiceResponses.add(tableInvoiceResponse);
        }
        Page<TableInvoiceResponse> tableInvoiceResponsePage = new PageImpl<>(tableInvoiceResponses, pageable, invoicePage.getTotalElements());
        logger.info("getInvoiceResponsesForTable - Invoice responses were got");
        return tableInvoiceResponsePage;
    }

    private Page<Invoice> getFilteredInvoices(Map<String, String> requestMap, Pageable pageable) {
        Specification<Invoice> invoiceSpecification = Specification.where(byDeleted());
        if (!requestMap.get("number").isEmpty()) {
            invoiceSpecification = invoiceSpecification.and(byNumberLike(requestMap.get("number")));
        }
        if (!requestMap.get("status").isEmpty()) {
            invoiceSpecification = invoiceSpecification.and(byStatus(InvoiceStatus.valueOf(requestMap.get("status"))));
        }
        if (!requestMap.get("apartmentNumber").isEmpty()) {
            invoiceSpecification = invoiceSpecification.and(byApartmentNumberLike(requestMap.get("apartmentNumber")));
        }
        if (!requestMap.get("ownerId").isEmpty()) {
            invoiceSpecification = invoiceSpecification.and(byOwnerId(Long.valueOf(requestMap.get("ownerId"))));
        }
        if (!requestMap.get("processed").isEmpty()) {
            invoiceSpecification = invoiceSpecification.and(byProcessed(Boolean.parseBoolean(requestMap.get("processed"))));
        }
        if (!requestMap.get("creationDate").isEmpty()) {
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.parse(requestMap.get("creationDate"), DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    LocalTime.MIDNIGHT);
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant dateFrom = zonedDateTime.toInstant();
            Instant dateTo = zonedDateTime.toInstant().plus(1, ChronoUnit.DAYS);
            invoiceSpecification = invoiceSpecification.and(byCreationDateGreaterThan(dateFrom)).and(byCreationDateLessThan(dateTo));
        }
        if (!requestMap.get("monthDate").isEmpty()) {
            String[] date = requestMap.get("monthDate").split("\\.");
            invoiceSpecification = invoiceSpecification.and(byMonth(Integer.valueOf(date[0])));
            invoiceSpecification = invoiceSpecification.and(byYear(Integer.valueOf(date[1])));
        }
        return invoiceRepo.findAll(invoiceSpecification, pageable);
    }

    @Override
    public InvoiceResponse getInvoiceResponse(Long id) {
        logger.info("getInvoiceResponse - Getting invoice response by id " + id);
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Invoice was not found by id " + id));
        BigDecimal totalPrice = invoiceItemRepo.getItemsSumByInvoiceId(id);
        List<InvoiceItem> invoiceItems = invoiceItemRepo.findAll(byInvoiceId(id));
        List<InvoiceItemResponse> itemResponses = invoiceItemMapper.invoiceItemListToInvoiceItemResponseList(invoiceItems);
        InvoiceResponse invoiceResponse = invoiceMapper.invoiceToInvoiceResponse(invoice, totalPrice, itemResponses);
        logger.info("getInvoiceResponse - Invoice response was got");
        return invoiceResponse;
    }

    @Override
    public void updateInvoice(Long id, InvoiceRequest invoiceRequest) {
        logger.info("updateInvoice - Updating invoice with id " + id + " " + invoiceRequest.toString());
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Invoice was not found by id " + id));
        Apartment apartment = apartmentRepo.findById(invoiceRequest.getApartmentId()).orElseThrow(() -> new EntityNotFoundException("Apartment was not found by id " + invoiceRequest.getApartmentId()));
        setNewApartmentBalance(apartment, invoiceRequest);
        invoiceMapper.updateInvoice(invoice, invoiceRequest, apartment);
        List<InvoiceItem> invoiceItems = invoiceItemRepo.findAll(byInvoiceId(id));
        invoiceItemRepo.deleteAll(invoiceItems);
        Invoice savedInvoice = invoiceRepo.save(invoice);
        saveInvoiceItems(invoiceRequest.getItemRequests(), savedInvoice);
        updateInvoiceCashSheet(invoice);
        logger.info("updateInvoice - Invoice was updated");
    }

    private void updateInvoiceCashSheet(Invoice invoice) {
        logger.info("updateInvoiceCashSheet - Updating invoice cash sheet for invoice with id " + invoice.getId());
        cashRegisterService.updateCashSheetFromInvoice(invoice);
        logger.info("updateInvoiceCashSheet - Invoice cash sheet was updated");
    }

    private void saveInvoiceItems(List<InvoiceItemRequest> itemRequests, Invoice invoice) {
        for (InvoiceItemRequest itemRequest : itemRequests) {
            com.example.myhouse24admin.entity.Service service = servicesRepo.findById(itemRequest.getServiceId()).orElseThrow(() -> new EntityNotFoundException("Service was not found by id " + itemRequest.getServiceId()));
            InvoiceItem invoiceItem = invoiceItemMapper.invoiceItemRequestToInvoiceItem(itemRequest, service, invoice);
            invoiceItemRepo.save(invoiceItem);
        }
    }

    @Override
    public ViewInvoiceResponse getInvoiceResponseForView(Long id) {
        logger.info("getInvoiceResponseForView - Getting invoice response for view by id "+id);
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Invoice was not found by id "+id));
        BigDecimal totalPrice = invoiceItemRepo.getItemsSumByInvoiceId(id);
        List<InvoiceItem> invoiceItems = invoiceItemRepo.findAll(byInvoiceId(id));
        List<InvoiceItemResponse> itemResponses = invoiceItemMapper.invoiceItemListToInvoiceItemResponseList(invoiceItems);
        ViewInvoiceResponse viewInvoiceResponse = invoiceMapper.invoiceToViewInvoiceResponse(invoice, itemResponses, totalPrice);
        logger.info("getInvoiceResponseForView - Invoice response was got");
        return viewInvoiceResponse;
    }

    @Override
    public boolean deleteInvoice(Long id) {
        logger.info("deleteInvoice - Deleting invoice by id " + id);
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Invoice was not found by id " + id));
        if (invoice.getPaid().compareTo(BigDecimal.valueOf(0)) != 0) {
            logger.info("deleteInvoice - Invoice has paid");
            return false;
        } else {
            invoice.setDeleted(true);
            invoiceRepo.save(invoice);
            logger.info("deleteInvoice - Invoice was deleted");
            return true;
        }
    }

    @Override
    public boolean deleteInvoices(Long[] invoiceIds) {
        logger.info("deleteInvoices - Deleting invoices by ids %s".formatted(Arrays.toString(invoiceIds)));
        List<Invoice> invoices = invoiceRepo.findAllById(List.of(invoiceIds));
        for (Invoice invoice : invoices) {
            if (invoice.getPaid().compareTo(BigDecimal.valueOf(0)) != 0) {
                logger.info("deleteInvoices - Invoice has paid");
                return false;
            } else {
                invoice.setDeleted(true);
                invoiceRepo.save(invoice);
            }
        }
        logger.info("deleteInvoices - Invoices were deleted");
        return true;
    }

    @Override
    public String getInvoiceNumber(Long id) {
        logger.info("getInvoiceNumber - Getting invoice number by id "+id);
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Invoice was not found by id "+id));
        String number = invoice.getNumber();
        logger.info("getInvoiceNumber - Invoice number was got");
        return number;
    }

    @Override
    public File createPdfFile(Long id, String template) {
        logger.info("createPdfFile - Creating pdf file with template "+template+" and by id "+id);
        XmlInvoiceDto xmlInvoiceDto = formxmlInvoiceDto(id);
        File pdfFile = pdfGenerator.formPdfFile(xmlInvoiceDto,template);
        logger.info("createPdfFile - Pdf file was created");
        return pdfFile;
    }

    private XmlInvoiceDto formxmlInvoiceDto(Long id) {
        List<InvoiceItem> invoiceItems = invoiceItemRepo.findAll(byInvoiceId(id));
        List<XmlListInvoiceItemDto> invoiceItemDtos = invoiceItemMapper.invoiceItemListToXmlListInvoiceItemDtoList(invoiceItems);
        XmlInvoiceItemsDto xmlInvoiceItemsDto = new XmlInvoiceItemsDto(invoiceItemDtos);
        BigDecimal totalPrice = invoiceItemRepo.getItemsSumByInvoiceId(id);
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Invoice was not found by id "+id));
        XmlInvoiceDto xmlInvoiceDto = invoiceMapper.invoiceToXmlInvoiceDto(invoice,
                xmlInvoiceItemsDto, totalPrice);
        return xmlInvoiceDto;
    }

}








