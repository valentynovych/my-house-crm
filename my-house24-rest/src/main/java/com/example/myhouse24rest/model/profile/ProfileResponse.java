package com.example.myhouse24rest.model.profile;

import com.example.myhouse24rest.model.apartment.ApartmentResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ProfileResponse(
        @Schema(example = "John Doe")
        String fullName,
        @Schema(example = "1234567890")
        String profileId,
        @Schema(example = "jdoe@example.com")
        String email,
        @Schema(example = "+123456123456")
        String phoneNumber,
        List<ApartmentResponse> myApartments) {
}
