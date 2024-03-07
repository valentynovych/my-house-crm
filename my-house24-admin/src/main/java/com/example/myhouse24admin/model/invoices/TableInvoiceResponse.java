package com.example.myhouse24admin.model.invoices;

import com.example.myhouse24admin.entity.InvoiceStatus;

import java.math.BigDecimal;

public record TableInvoiceResponse(
        Long id,
        String number,
        InvoiceStatus status,
        String creationDate,
        String apartment,
        String ownerFullName,
        boolean isProcessed,
        BigDecimal paid,
        BigDecimal totalPrice
) {
}
