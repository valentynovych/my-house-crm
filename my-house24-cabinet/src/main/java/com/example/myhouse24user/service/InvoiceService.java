package com.example.myhouse24user.service;

import com.example.myhouse24user.model.invoice.InvoiceResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface InvoiceService {
    Page<InvoiceResponse> getInvoiceResponses(Map<String, String> requestMap);
}
