package com.example.myhouse24user.model.authentication;

import com.example.myhouse24user.validators.emailValidation.authentication.EmailExist;
import jakarta.validation.constraints.NotBlank;
public record EmailRequest(
        @EmailExist
        @NotBlank(message = "Обов'язкове поле")
        String email) {
}
