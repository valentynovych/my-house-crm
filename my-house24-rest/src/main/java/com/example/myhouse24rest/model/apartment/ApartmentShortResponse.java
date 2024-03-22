package com.example.myhouse24rest.model.apartment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ApartmentShortResponse(
        @Schema(example = "1")
        Long apartmentId,
        @Schema(example = "00001")
        String apartmentNumber,
        @Schema(example = "-100.00")
        BigDecimal balance) {
}
