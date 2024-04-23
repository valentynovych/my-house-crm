package com.example.myhouse24admin.model.invoices;

public record AccountNumberResponse(
        Long id,
        String accountNumber,
        boolean deleted
) {
}
