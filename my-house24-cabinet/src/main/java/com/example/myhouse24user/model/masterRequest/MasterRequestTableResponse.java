package com.example.myhouse24user.model.masterRequest;

import com.example.myhouse24user.entity.MasterRequestStatus;
import com.example.myhouse24user.entity.Role;

import java.time.Instant;

public record MasterRequestTableResponse(
        Long id,
        Instant visitDate,
        MasterRequestStatus status,
        String description,
        String masterType) {
}
