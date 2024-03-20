package com.example.myhouse24user.model.messages;

import java.time.Instant;

public record OwnerMessageResponse(
        Long id,
        String staffFullName,
        String text,
        String subject,
        boolean isRead,
        Instant sendDate) {
}
