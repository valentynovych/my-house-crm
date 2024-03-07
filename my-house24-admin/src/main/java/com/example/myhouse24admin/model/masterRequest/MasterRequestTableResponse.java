package com.example.myhouse24admin.model.masterRequest;

import com.example.myhouse24admin.entity.MasterRequestStatus;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import com.example.myhouse24admin.model.staff.StaffShortResponse;

import java.time.Instant;

public record MasterRequestTableResponse(
        Long id,
        Instant visitDate,
        String description,
        ApartmentResponse apartment,
        String apartmentOwnerPhone,
        StaffShortResponse master,
        MasterRequestStatus status) {
}
