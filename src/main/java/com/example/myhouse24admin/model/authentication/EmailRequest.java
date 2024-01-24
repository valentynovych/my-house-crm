package com.example.myhouse24admin.model.authentication;

import com.example.myhouse24admin.validators.emailValidation.EmailExist;
import jakarta.validation.constraints.NotBlank;
public record EmailRequest(
        @EmailExist
        @NotBlank(message = "Обов'язкове поле")
        String email) {
}
