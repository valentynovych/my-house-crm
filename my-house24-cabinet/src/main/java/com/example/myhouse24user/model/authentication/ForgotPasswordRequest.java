package com.example.myhouse24user.model.authentication;

import com.example.myhouse24user.validators.passwordValidation.PasswordsMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@PasswordsMatch(
        password = "password",
        confirmPassword = "confirmPassword"
)
public record ForgotPasswordRequest(
        @NotBlank(message = "Поле не може бути порожнім")
        @Pattern.List({
                @Pattern(regexp = ".{8,}", message = "Довжина паролю має бути більше 8 символів"),
                @Pattern(regexp = ".*\\d+.*", message = "Пароль має мати принаймні одну цифру, одну велику літеру, один спецсимвол ,./?"),
                @Pattern(regexp = ".*[,./?]+.*", message = "Пароль має мати принаймні одну цифру, одну велику літеру, один спецсимвол ,./?"),
                @Pattern(regexp = ".*[A-Z]+.*", message = "Пароль має мати принаймні одну цифру, одну велику літеру, один спецсимвол ,./?")
        })
        String password,
        @NotBlank(message = "Поле не може бути порожнім")
        String confirmPassword
) {
}
