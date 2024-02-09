package com.example.myhouse24admin.model.apartmentOwner;

import com.example.myhouse24admin.entity.OwnerStatus;

public record ViewApartmentOwnerResponse(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        String birthDate,
        OwnerStatus status,
        String aboutOwner,
        String phoneNumber,
        String viberNumber,
        String telegramUsername,
        String email,
        String avatar
) {
}
