package com.example.myhouse24rest.model.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AuthRequest(
        @Schema(example = "user@example.com")
        @NotNull(message = "Email cannot be null")
        @NotEmpty(message = "Email cannot be empty")
        @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email")
        String email,
        @Schema(example = "password1!A3Q")
        @NotEmpty(message = "Password cannot be empty")
        @NotNull(message = "Password cannot be null")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=,./?])(?=\\S+$).{8,}$", message = "Invalid password")
        String password) {
}
