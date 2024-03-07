package com.example.myhouse24admin.model.staff;

import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.entity.StaffStatus;

public record StaffResponse(
        Long id,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        Role role,
        StaffStatus status
) {
}
