package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.Invoice;
import com.example.myhouse24admin.entity.InvoiceItem;
import com.example.myhouse24admin.entity.InvoiceStatus;
import com.example.myhouse24admin.mapper.ApartmentOwnerMapper;
import com.example.myhouse24admin.mapper.InvoiceItemMapper;
import com.example.myhouse24admin.mapper.InvoiceMapper;
import com.example.myhouse24admin.model.invoices.*;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.InvoiceItemRepo;
import com.example.myhouse24admin.repository.InvoiceRepo;
import com.example.myhouse24admin.repository.ServicesRepo;
import com.example.myhouse24admin.service.InvoiceService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
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
    private final Logger logger = LogManager.getLogger(InvoiceServiceImpl.class);

    public InvoiceServiceImpl(InvoiceRepo invoiceRepo,
                              InvoiceItemRepo invoiceItemRepo,
                              ApartmentRepo apartmentRepo,
                              ServicesRepo servicesRepo,
                              ApartmentOwnerMapper apartmentOwnerMapper,
                              InvoiceMapper invoiceMapper,
                              InvoiceItemMapper invoiceItemMapper) {
        this.invoiceRepo = invoiceRepo;
        this.invoiceItemRepo = invoiceItemRepo;
        this.apartmentRepo = apartmentRepo;
        this.servicesRepo = servicesRepo;
        this.apartmentOwnerMapper = apartmentOwnerMapper;
        this.invoiceMapper = invoiceMapper;
        this.invoiceItemMapper = invoiceItemMapper;
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
        Integer numberPart = Integer.valueOf(number);
        numberPart += 1;
        String newNumber = "";
        for (int i = 0; i < 10 - numberPart.toString().length(); i++) {
            newNumber += "0";
        }
        return newNumber + numberPart;
    }

    @Override
    public OwnerResponse getOwnerResponse(Long apartmentId) {
        logger.info("getOwnerResponse - Getting owner response by apartment id "+apartmentId);
        Apartment apartment = apartmentRepo.findById(apartmentId).orElseThrow(() -> new EntityNotFoundException("Apartment was not found by id "+apartmentId));
        OwnerResponse ownerResponse = apartmentOwnerMapper.apartmentToOwnerResponse(apartment);
        logger.info("getOwnerResponse - Owner response was got");
        return ownerResponse;
    }

    @Override
    public void createInvoice(InvoiceRequest invoiceRequest) {
        logger.info("createInvoice - Creating new invoice "+invoiceRequest.toString());
        Apartment apartment = apartmentRepo.findById(invoiceRequest.getApartmentId()).orElseThrow(()-> new EntityNotFoundException("Apartment was not found by id "+invoiceRequest.getApartmentId()));
        setNewApartmentBalance(apartment, invoiceRequest);
        String number = createNumber();
        Invoice invoice = invoiceMapper.invoiceRequestToInvoice(invoiceRequest,
                apartment, number);
        Invoice savedInvoice = invoiceRepo.save(invoice);
        saveInvoiceItems(invoiceRequest.getItemRequests(), savedInvoice);
        logger.info("createInvoice - Invoice was created");
    }

    private void setNewApartmentBalance(Apartment apartment, InvoiceRequest invoiceRequest) {
        BigDecimal remainder = invoiceRequest.getPaid().subtract(invoiceRequest.getTotalPrice());
        BigDecimal newBalance = apartment.getBalance().add(remainder);
        apartment.setBalance(newBalance);
    }

    @Override
    public Page<TableInvoiceResponse> getInvoiceResponsesForTable(Map<String, String> requestMap) {
        logger.info("getInvoiceResponsesForTable - Getting invoice responses for table "+requestMap.toString());
        Pageable pageable = PageRequest.of(Integer.valueOf(requestMap.get("page")), Integer.valueOf(requestMap.get("pageSize")));
        Page<Invoice> invoicePage = getFilteredInvoices(requestMap, pageable);
        List<TableInvoiceResponse> tableInvoiceResponses = new ArrayList<>();
        for(Invoice invoice: invoicePage.getContent()){
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
        if(!requestMap.get("number").isEmpty()){
            invoiceSpecification = invoiceSpecification.and(byNumberLike(requestMap.get("number")));
        }
        if(!requestMap.get("status").isEmpty()){
            invoiceSpecification = invoiceSpecification.and(byStatus(InvoiceStatus.valueOf(requestMap.get("status"))));
        }
        if (!requestMap.get("apartmentNumber").isEmpty()){
            invoiceSpecification = invoiceSpecification.and(byApartmentNumberLike(requestMap.get("apartmentNumber")));
        }
        if (!requestMap.get("ownerId").isEmpty()){
            invoiceSpecification = invoiceSpecification.and(byOwnerId(Long.valueOf(requestMap.get("ownerId"))));
        }
        if (!requestMap.get("processed").isEmpty()){
            invoiceSpecification = invoiceSpecification.and(byProcessed(Boolean.parseBoolean(requestMap.get("processed"))));
        }
        if (!requestMap.get("creationDate").isEmpty()){
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.parse(requestMap.get("creationDate"), DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    LocalTime.MIDNIGHT);
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant dateFrom = zonedDateTime.toInstant();
            Instant dateTo = zonedDateTime.toInstant().plus(1, ChronoUnit.DAYS);
            invoiceSpecification = invoiceSpecification.and(byCreationDateGreaterThan(dateFrom)).and(byCreationDateLessThan(dateTo));
        }
        if (!requestMap.get("monthDate").isEmpty()){
            String [] date = requestMap.get("monthDate").split("\\.");
            invoiceSpecification = invoiceSpecification.and(byMonth(Integer.valueOf(date[0])));
            invoiceSpecification = invoiceSpecification.and(byYear(Integer.valueOf(date[1])));
        }
        return invoiceRepo.findAll(invoiceSpecification, pageable);
    }

    @Override
    public InvoiceResponse getInvoiceResponse(Long id) {
        logger.info("getInvoiceResponse - Getting invoice response by id "+id);
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Invoice was not found by id "+id));
        BigDecimal totalPrice = invoiceItemRepo.getItemsSumByInvoiceId(id);
        List<InvoiceItem> invoiceItems = invoiceItemRepo.findAll(byInvoiceId(id));
        List<InvoiceItemResponse> itemResponses = invoiceItemMapper.invoiceItemListToInvoiceItemResponseList(invoiceItems);
        InvoiceResponse invoiceResponse = invoiceMapper.invoiceToInvoiceResponse(invoice,totalPrice, itemResponses);
        logger.info("getInvoiceResponse - Invoice response was got");
        return invoiceResponse;
    }

    @Override
    public void updateInvoice(Long id, InvoiceRequest invoiceRequest) {
        logger.info("updateInvoice - Updating invoice with id "+id+" "+invoiceRequest.toString());
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Invoice was not found by id "+id));
        Apartment apartment = apartmentRepo.findById(invoiceRequest.getApartmentId()).orElseThrow(()-> new EntityNotFoundException("Apartment was not found by id "+invoiceRequest.getApartmentId()));
        setNewApartmentBalance(apartment, invoiceRequest);
        invoiceMapper.updateInvoice(invoice, invoiceRequest, apartment);
        List<InvoiceItem> invoiceItems = invoiceItemRepo.findAll(byInvoiceId(id));
        invoiceItemRepo.deleteAll(invoiceItems);
        Invoice savedInvoice = invoiceRepo.save(invoice);
        saveInvoiceItems(invoiceRequest.getItemRequests(), savedInvoice);
        logger.info("updateInvoice - Invoice was updated");
    }
    private void saveInvoiceItems(List<InvoiceItemRequest> itemRequests, Invoice invoice) {
        for(InvoiceItemRequest itemRequest: itemRequests){
            com.example.myhouse24admin.entity.Service service = servicesRepo.findById(itemRequest.getServiceId()).orElseThrow(()-> new EntityNotFoundException("Service was not found by id "+itemRequest.getServiceId()));
            InvoiceItem invoiceItem = invoiceItemMapper.invoiceItemRequestToInvoiceItem(itemRequest, service, invoice);
            invoiceItemRepo.save(invoiceItem);
        }
    }

    @Override
    public ViewInvoiceResponse getInvoiceResponseForView(Long id) {
        logger.info("getInvoiceResponseForView - Getting invoice response for view by id "+id);
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Invoice was not found by id "+id));
        System.out.println(invoice.getApartment().getPersonalAccount().getAccountNumber());
        BigDecimal totalPrice = invoiceItemRepo.getItemsSumByInvoiceId(id);
        List<InvoiceItem> invoiceItems = invoiceItemRepo.findAll(byInvoiceId(id));
        List<InvoiceItemResponse> itemResponses = invoiceItemMapper.invoiceItemListToInvoiceItemResponseList(invoiceItems);
        ViewInvoiceResponse viewInvoiceResponse = invoiceMapper.invoiceToViewInvoiceResponse(invoice, itemResponses, totalPrice);
        logger.info("getInvoiceResponseForView - Invoice response was got");
        return viewInvoiceResponse;
    }

    @Override
    public boolean deleteInvoice(Long id) {
        logger.info("deleteInvoice - Deleting invoice by id "+id);
        Invoice invoice = invoiceRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Invoice was not found by id "+id));
        if(invoice.getPaid().compareTo(BigDecimal.valueOf(0)) != 0){
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
        for(Invoice invoice: invoices){
            if(invoice.getPaid().compareTo(BigDecimal.valueOf(0)) != 0){
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
}
