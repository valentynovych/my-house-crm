package com.example.myhouse24admin.model.apartmentOwner;

import com.example.myhouse24admin.entity.OwnerStatus;

public record TableApartmentOwnerResponse(
        Long id,
        String fullName,
        String phoneNumber,
        String email,
        String house, // todo get house
        String apartment, //todo get apartment
        String creationDate,
        OwnerStatus status,
        boolean hasDebt // todo get has debt
) {
}
