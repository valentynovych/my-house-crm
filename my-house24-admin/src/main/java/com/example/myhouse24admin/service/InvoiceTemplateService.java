package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateListRequest;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateResponse;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.List;

public interface InvoiceTemplateService {
    void updateTemplates(InvoiceTemplateListRequest invoiceTemplateListRequest);
    List<InvoiceTemplateResponse> getInvoiceTemplatesResponses();
    void setDefaultInvoice(Long id);
    File getTemplateFile(String fileName);
    MediaType getMediaTypeForFileName(String fileName);
}
