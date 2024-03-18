package com.example.myhouse24user.model.staff;

import com.example.myhouse24user.entity.Role;

public record StaffShortResponse(
        Long id,
        String fullName,
        Role role) {
}
