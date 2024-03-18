package com.example.myhouse24user.service;

import com.example.myhouse24user.model.invoice.InvoiceResponse;
import com.example.myhouse24user.model.invoice.ViewInvoiceResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface InvoiceService {
    Page<InvoiceResponse> getInvoiceResponses(Map<String, String> requestMap);
    ViewInvoiceResponse getViewInvoiceResponse(Long id);
    byte[] createPdfFile(Long id);
}
