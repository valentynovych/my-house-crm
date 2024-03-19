package com.example.myhouse24user.model.invoice;

import com.example.myhouse24user.entity.InvoiceStatus;

import java.math.BigDecimal;

public record InvoiceResponse(
        Long id,
        String number,
        String creationDate,
        InvoiceStatus status,
        BigDecimal paid,
        BigDecimal totalPrice
) {
}
