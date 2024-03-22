package com.example.myhouse24rest.model.apartment;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApartmentResponse(
        @Schema(example = "1")
        Long apartmentId,
        @Schema(example = "00001")
        String apartmentNumber,
        @Schema(example = "My House")
        String houseName,
        @Schema(example = "street 1, street 2")
        String address,
        @Schema(example = "1")
        String section,
        @Schema(example = "1")
        String floor,
        @Schema(example = "00000-00001")
        String personalAccountNumber) {
}
