package com.example.myhouse24user.model.owner;

import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.validators.emailValidation.owner.EmailUnique;
import com.example.myhouse24user.validators.passwordValidation.PasswordsMatch;
import com.example.myhouse24user.validators.phoneValidation.PhoneUnique;
import com.example.myhouse24user.validators.socialsValidation.telegram.TelegramUnique;
import com.example.myhouse24user.validators.socialsValidation.viber.ViberUnique;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
@PasswordsMatch(
        password = "password",
        confirmPassword = "confirmPassword",
        message = "{validation-confirm-password-match}"
)
@EmailUnique(
        email = "email",
        id = "id",
        message = "{validation-email-exist}"
)
@TelegramUnique(
        telegramUsername = "telegramUsername",
        id = "id",
        message = "{validation-telegram-exist}"
)
@ViberUnique(
        viberNumber = "viberNumber",
        id = "id",
        message = "{validation-viber-exist}"
)
@PhoneUnique(
        phoneNumber = "phoneNumber",
        id = "id",
        message = "{validation-phone-exist}"
)
public record ApartmentOwnerRequest(
        Long id,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 50, message = "{validation-size-max}")
        String firstName,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 50, message = "{validation-size-max}")
        String lastName,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 50, message = "{validation-size-max}")
        String middleName,
        @NotNull(message = "{validation-not-empty}")
        String birthDate,
        @NotNull(message = "{validation-not-empty}")
        OwnerStatus status,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 300, message = "{validation-size-max}")
        String aboutOwner,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 13, message = "{validation-size-max}")
        @Pattern(regexp = "\\+?380(50)?(66)?(95)?(99)?(67)?(68)?(96)?(97)?(98)?(63)?(93)?(73)?[0-9]{0,7}", message = "{validation-phone-from-pattern}")
        String phoneNumber,
        @Size(max = 13, message = "{validation-size-max}")
        String viberNumber,
        @Size(max = 50, message = "{validation-size-max}")
        String telegramUsername,
        @NotBlank(message = "{validation-not-empty}")
        @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-z]{2,3}(\\.[a-z]{2,3})?", message = "{validation-email-from-pattern}")
        String email,
        String password,
        String confirmPassword
) {
}
