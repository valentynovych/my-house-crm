package com.example.myhouse24admin.model.apartmentOwner;

import com.example.myhouse24admin.entity.OwnerStatus;
import com.example.myhouse24admin.validators.emailValidation.owners.EditEmailFieldUnique;
import com.example.myhouse24admin.validators.passwordValidation.PasswordsMatch;
import com.example.myhouse24admin.validators.phoneValidation.owners.EditPhoneFieldUnique;
import com.example.myhouse24admin.validators.socialsValidation.telegram.EditTelegramFieldUnique;
import com.example.myhouse24admin.validators.socialsValidation.telegram.TelegramFieldUnique;
import com.example.myhouse24admin.validators.socialsValidation.viber.EditViberFieldUnique;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
@PasswordsMatch(
        password = "password",
        confirmPassword = "confirmPassword",
        message = "{validation-confirm-password-match}"
)
public record EditApartmentOwnerRequest(
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
        @EditPhoneFieldUnique(message = "{validation-phone-exist}")
        String phoneNumber,
        @Size(max = 13, message = "{validation-size-max}")
        @EditViberFieldUnique(message = "{validation-viber-exist}")
        String viberNumber,
        @Size(max = 50, message = "{validation-size-max}")
        @EditTelegramFieldUnique(message = "{validation-telegram-exist}")
        String telegramUsername,
        @NotBlank(message = "{validation-not-empty}")
        @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-z]{2,3}(\\.[a-z]{2,3})?", message = "{validation-email-from-pattern}")
        @EditEmailFieldUnique(message = "{validation-email-exist}")
        String email,
        String password,
        String confirmPassword

) {
}
