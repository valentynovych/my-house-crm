package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.Invoice;
import com.example.myhouse24admin.entity.InvoiceItem;
import com.example.myhouse24admin.mapper.ApartmentOwnerMapper;
import com.example.myhouse24admin.mapper.InvoiceItemMapper;
import com.example.myhouse24admin.mapper.InvoiceMapper;
import com.example.myhouse24admin.model.invoices.InvoiceItemRequest;
import com.example.myhouse24admin.model.invoices.InvoiceRequest;
import com.example.myhouse24admin.model.invoices.OwnerResponse;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.InvoiceItemRepo;
import com.example.myhouse24admin.repository.InvoiceRepo;
import com.example.myhouse24admin.repository.ServicesRepo;
import com.example.myhouse24admin.service.InvoiceService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        logger.info("getOwnerResponse - Owner response was created");
        return ownerResponse;
    }

    @Override
    public void createInvoice(InvoiceRequest invoiceRequest) {
        Apartment apartment = apartmentRepo.findById(invoiceRequest.getApartmentId()).orElseThrow(()-> new EntityNotFoundException("Apartment was not found by id "+invoiceRequest.getApartmentId()));
        String number = createNumber();
        Invoice invoice = invoiceMapper.invoiceRequestToInvoice(invoiceRequest,
                apartment.getPersonalAccount(), apartment.getTariff(), number);
        Invoice savedInvoice = invoiceRepo.save(invoice);
        saveInvoiceItems(invoiceRequest.getItemRequests(), savedInvoice);
    }

    private void saveInvoiceItems(List<InvoiceItemRequest> itemRequests, Invoice invoice) {
        for(InvoiceItemRequest itemRequest: itemRequests){
            com.example.myhouse24admin.entity.Service service = servicesRepo.findById(itemRequest.getServiceId()).orElseThrow(()-> new EntityNotFoundException("Service was not found by id "+itemRequest.getServiceId()));
            InvoiceItem invoiceItem = invoiceItemMapper.invoiceItemRequestToInvoiceItem(itemRequest, service, invoice);
            invoiceItemRepo.save(invoiceItem);
        }
    }
}
