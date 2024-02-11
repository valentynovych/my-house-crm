package com.example.myhouse24admin.model.apartmentOwner;

import com.example.myhouse24admin.entity.OwnerStatus;

public record FilterRequest(
        String ownerId,
        String fullName,
        String phoneNumber,
        String email,
        Long houseId,
        String apartment,
        String creationDate,
        OwnerStatus status,
        Boolean debt
) {
}
