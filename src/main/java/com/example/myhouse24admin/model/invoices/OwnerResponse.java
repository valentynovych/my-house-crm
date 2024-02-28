package com.example.myhouse24admin.model.invoices;

public record OwnerResponse(
        Long accountNumber,
        String ownerFullName,
        String ownerPhoneNumber,
        Long tariffId,
        String tariffName

) {
}
