package com.example.myhouse24admin.model.invoiceTemplate;

import jakarta.validation.Valid;

import java.util.List;

public class InvoiceTemplateListRequest {
    @Valid
    private List<InvoiceTemplateRequest> invoiceTemplates;
    private List<Long> idsToDelete;
    public List<InvoiceTemplateRequest> getInvoiceTemplates() {
        return invoiceTemplates;
    }

    public void setInvoiceTemplates(List<InvoiceTemplateRequest> invoiceTemplates) {
        this.invoiceTemplates = invoiceTemplates;
    }

    public List<Long> getIdsToDelete() {
        return idsToDelete;
    }

    public void setIdsToDelete(List<Long> idsToDelete) {
        this.idsToDelete = idsToDelete;
    }
}
