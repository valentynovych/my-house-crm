package com.example.myhouse24admin.model.invoices;

public record OwnerResponse(
        String accountNumber,
        String ownerFullName,
        String ownerPhoneNumber,
        Long tariffId,
        String tariffName

) {
}
