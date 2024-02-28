package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.invoices.InvoiceRequest;
import com.example.myhouse24admin.model.invoices.OwnerResponse;

public interface InvoiceService {
    String createNumber();
    OwnerResponse getOwnerResponse(Long apartmentId);
    void createInvoice(InvoiceRequest invoiceRequest);
}
