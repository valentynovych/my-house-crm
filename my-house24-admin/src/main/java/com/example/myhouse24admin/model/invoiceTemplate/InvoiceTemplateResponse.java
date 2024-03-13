package com.example.myhouse24admin.model.invoiceTemplate;

public record InvoiceTemplateResponse(
        Long id,
        String name,
        String file,
        boolean isDefault
) {
}
