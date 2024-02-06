package com.example.myhouse24admin.model.apartmentOwner;

import com.example.myhouse24admin.entity.OwnerStatus;

import java.time.Instant;

public record ApartmentOwnerResponse(
        String firstName,
        String lastName,
        String middleName,
        Instant birthDate,
        OwnerStatus status,
        String aboutOwner,
        String phoneNumber,
        String viberNumber,
        String telegramUsername,
        String email,
        String image

) {
}
