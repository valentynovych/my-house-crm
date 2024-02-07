package com.example.myhouse24admin.model.apartmentOwner;

import com.example.myhouse24admin.entity.OwnerStatus;

public record FilterRequest(
        Long id,
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
