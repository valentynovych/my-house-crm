package com.example.myhouse24admin.model.apartmentOwner;

import com.example.myhouse24admin.entity.OwnerStatus;
import com.example.myhouse24admin.validators.emailValidation.owners.OwnerEmailFieldUnique;
import com.example.myhouse24admin.validators.passwordValidation.PasswordsMatch;
import com.example.myhouse24admin.validators.phoneValidation.owners.OwnerPhoneFieldUnique;
import com.example.myhouse24admin.validators.socialsValidation.TelegramFieldUnique;
import com.example.myhouse24admin.validators.socialsValidation.ViberFieldUnique;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
@PasswordsMatch(
        password = "password",
        confirmPassword = "confirmPassword"
)
public record CreateApartmentOwnerRequest(
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 50, message = "{validation-size-max}")
        String firstName,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 50, message = "{validation-size-max}")
        String lastName,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 50, message = "{validation-size-max}")
        String middleName,
        @NotBlank(message = "{validation-not-empty}")
        String birthDate,
        @NotNull(message = "{validation-not-empty}")
        OwnerStatus status,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 300, message = "{validation-size-max}")
        String aboutOwner,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 13, message = "{validation-size-max}")
        @Pattern(regexp = "\\+?380(50)?(66)?(95)?(99)?(67)?(68)?(96)?(97)?(98)?(63)?(93)?(73)?[0-9]{0,7}", message = "{validation-phone-from-pattern}")
        @OwnerPhoneFieldUnique(message = "{validation-phone-exist}")
        String phoneNumber,
        @Size(max = 13, message = "{validation-size-max}")
        @ViberFieldUnique(message = "{validation-viber-exist}")
        String viberNumber,
        @Size(max = 50, message = "{validation-size-max}")
        @TelegramFieldUnique(message = "{validation-telegram-exist}")
        String telegramUsername,
        @NotBlank(message = "{validation-not-empty}")
        @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-z]{2,3}(\\.[a-z]{2,3})?", message = "{validation-email-from-pattern}")
        @OwnerEmailFieldUnique(message = "{validation-email-exist}")
        String email,
        @Pattern.List({
                @Pattern(regexp = ".*\\d+.*", message = "{validation-password-regex}"),
                @Pattern(regexp = ".*[,./?]+.*", message = "{validation-password-regex}"),
                @Pattern(regexp = ".*[A-Z]+.*", message = "{validation-password-regex}")
        })
        @Size(min = 8, max = 100, message = "{validation-size-min-max}")
        @NotBlank(message = "{validation-not-empty}")
        String password,
        @NotBlank(message = "{validation-not-empty}")
        String confirmPassword

) {
}
