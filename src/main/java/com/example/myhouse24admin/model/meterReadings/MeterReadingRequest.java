package com.example.myhouse24admin.model.meterReadings;

import com.example.myhouse24admin.entity.MeterReadingStatus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record MeterReadingRequest(
        @NotBlank(message = "{validation-not-empty}")
        String creationDate,
        @NotNull(message = "{validation-not-empty}")
        MeterReadingStatus status,
        @NotNull(message = "{validation-not-empty}")
        @Digits(integer = 5, fraction = 4, message = "{validation-price-max-length}")
        BigDecimal readings,
        @NotNull(message = "{validation-not-empty}")
        Long apartmentId,
        @NotNull(message = "{validation-not-empty}")
        Long serviceId
) {
}
