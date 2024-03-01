package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.invoices.InvoiceRequest;
import com.example.myhouse24admin.model.invoices.InvoiceResponse;
import com.example.myhouse24admin.model.invoices.OwnerResponse;
import com.example.myhouse24admin.model.invoices.TableInvoiceResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface InvoiceService {
    String createNumber();
    OwnerResponse getOwnerResponse(Long apartmentId);
    void createInvoice(InvoiceRequest invoiceRequest);
    Page<TableInvoiceResponse> getInvoiceResponsesForTable(Map<String,String> requestMap);
    InvoiceResponse getInvoiceResponse(Long id);
    void updateInvoice(Long id, InvoiceRequest invoiceRequest);
}
