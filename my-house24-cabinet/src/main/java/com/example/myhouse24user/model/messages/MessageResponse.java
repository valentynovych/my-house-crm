package com.example.myhouse24user.model.messages;

import com.example.myhouse24user.entity.Staff;
import com.example.myhouse24user.model.staff.StaffShortResponse;

import java.time.Instant;

public record MessageResponse(
        Long id,
        Instant sendDate,
        String subject,
        String text,
        StaffShortResponse staff) {
}

