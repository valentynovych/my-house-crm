package com.example.myhouse24admin.model.apartmentOwner;

import com.example.myhouse24admin.validators.emailValidation.owners.OwnerEmailFieldUnique;
import jakarta.validation.constraints.NotBlank;

public record InvitationRequest(
        @NotBlank(message = "{validation-not-empty}")
        @OwnerEmailFieldUnique(message = "{validation-owner-exist}")
        String email
) {
}
