package com.example.myhouse24rest.model.message;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MessageResponse(
        @Schema(example = "1")
        Long messageId,
        @Schema(example = "2022-01-01T00:00:00")
        LocalDateTime sendDate,
        @Schema(example = "Subject")
        String subject,
        @Schema(example = "Text")
        String text,
        @Schema(example = "From")
        String fromStaff,
        @Schema(example = "true")
        boolean read) {
}
