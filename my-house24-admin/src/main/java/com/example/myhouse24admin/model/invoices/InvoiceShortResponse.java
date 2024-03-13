package com.example.myhouse24admin.model.invoices;

import java.time.Instant;

public record InvoiceShortResponse(
        Long id,
        String number,
        Instant creationDate) {
}
