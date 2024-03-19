package com.example.myhouse24user.model.owner;

import com.example.myhouse24user.entity.OwnerStatus;

public record EditOwnerResponse(
        Long id,
        String ownerId,
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
        String image
) {
}
