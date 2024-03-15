package com.example.myhouse24user.model.authentication;

import com.example.myhouse24user.validators.emailValidation.registration.OwnerEmailFieldUnique;
import com.example.myhouse24user.validators.passwordValidation.PasswordsMatch;
import com.example.myhouse24user.validators.policyValidation.PolicyTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
@PasswordsMatch(
        password = "password",
        confirmPassword = "confirmPassword"
)
public record RegistrationRequest(
        @NotBlank(message = "Обов'язкове поле")
        String firstName,
        @NotBlank(message = "Обов'язкове поле")
        String lastName,
        @NotBlank(message = "Обов'язкове поле")
        String middleName,
        @NotBlank(message = "Обов'язкове поле")
        @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-z]{2,3}(\\.[a-z]{2,3})?", message = "Email не відповідає формату")
        @OwnerEmailFieldUnique
        String email,
        @NotBlank(message = "Обов'язкове поле")
        @Pattern.List({
                @Pattern(regexp = ".{8,}", message = "Довжина паролю має бути більше 8 символів"),
                @Pattern(regexp = ".*\\d+.*", message = "Пароль має мати принаймні одну цифру, одну велику літеру, один спецсимвол ,./?"),
                @Pattern(regexp = ".*[,./?]+.*", message = "Пароль має мати принаймні одну цифру, одну велику літеру, один спецсимвол ,./?"),
                @Pattern(regexp = ".*[A-Z]+.*", message = "Пароль має мати принаймні одну цифру, одну велику літеру, один спецсимвол ,./?")
        })
        String password,
        @NotBlank(message = "Обов'язкове поле")
        String confirmPassword,
        @PolicyTrue
        boolean policy
) {
}
