package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.Invoice;
import com.example.myhouse24user.entity.InvoiceStatus;
import com.example.myhouse24user.mapper.InvoiceItemRepo;
import com.example.myhouse24user.mapper.InvoiceMapper;
import com.example.myhouse24user.model.invoice.InvoiceResponse;
import com.example.myhouse24user.repository.InvoiceRepo;
import com.example.myhouse24user.service.InvoiceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.myhouse24user.specification.InvoiceSpecification.*;
@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepo invoiceRepo;
    private final InvoiceItemRepo invoiceItemRepo;
    private final InvoiceMapper invoiceMapper;
    private final Logger logger = LogManager.getLogger(InvoiceServiceImpl.class);
    public InvoiceServiceImpl(InvoiceRepo invoiceRepo,
                              InvoiceItemRepo invoiceItemRepo,
                              InvoiceMapper invoiceMapper) {
        this.invoiceRepo = invoiceRepo;
        this.invoiceItemRepo = invoiceItemRepo;
        this.invoiceMapper = invoiceMapper;
    }

    @Override
    public Page<InvoiceResponse> getInvoiceResponses(Map<String, String> requestMap) {
        logger.info("getInvoiceResponses - Getting invoice responses with request "+requestMap.toString());
        Pageable pageable = PageRequest.of(Integer.parseInt(requestMap.get("page")),
                Integer.parseInt(requestMap.get("pageSize")));
        Page<Invoice> invoicePage = getFilteredInvoices(requestMap, pageable);
        List<InvoiceResponse> invoiceResponses = new ArrayList<>();
        for(Invoice invoice: invoicePage.getContent()) {
            BigDecimal totalPrice = invoiceItemRepo.getItemsSumByInvoiceId(invoice.getId());
            InvoiceResponse invoiceResponse = invoiceMapper.invoiceToInvoiceResponse(invoice,totalPrice);
            invoiceResponses.add(invoiceResponse);
        }
        Page<InvoiceResponse> invoiceResponsePage = new PageImpl<>(invoiceResponses, pageable, invoicePage.getTotalElements());
        logger.info("getInvoiceResponses - Invoice responses were got");
        return invoiceResponsePage;
    }

    private Page<Invoice> getFilteredInvoices(Map<String, String> requestMap, Pageable pageable) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String email = userDetails.getUsername();
        Specification<Invoice> invoiceSpecification = Specification.where(byDeleted())
                .and(byOwnerEmail(email)).and(byProcessedTrue());
        if(!requestMap.get("number").isEmpty()){
            invoiceSpecification = invoiceSpecification.and(byNumber(requestMap.get("number")));
        }
        if(!requestMap.get("status").isEmpty()){
            invoiceSpecification = invoiceSpecification.and(byStatus(InvoiceStatus.valueOf(requestMap.get("status"))));
        }
        if(!requestMap.get("date").isEmpty()){
            LocalDate localDate = LocalDate.parse(requestMap.get("date"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            invoiceSpecification = invoiceSpecification.and(byCreationDate(localDate));
        }
        if(!requestMap.get("apartmentId").isEmpty()){
            invoiceSpecification = invoiceSpecification.and(byApartmentId(Long.valueOf(requestMap.get("apartmentId"))));
        }

        return invoiceRepo.findAll(invoiceSpecification, pageable);
    }
}
