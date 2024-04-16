package com.example.myhouse24admin.model.siteManagement.contacts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactsPageDto(
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 100, message = "{validation-size-max}")
        String title,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 2000, message = "{validation-size-max}")
        String text,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 200, message = "{validation-size-max}")
        String linkToSite,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 200, message = "{validation-size-max}")
        String fullName,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 200, message = "{validation-size-max}")
        String location,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 200, message = "{validation-size-max}")
        String address,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 13, message = "{validation-size-max}")
        @Pattern(regexp = "\\+?380(50)?(66)?(95)?(99)?(67)?(68)?(96)?(97)?(98)?(63)?(93)?(73)?[0-9]{0,7}", message = "{validation-phone-from-pattern}")
        String phoneNumber,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 100, message = "{validation-size-max}")
        String email,
        @NotBlank(message = "{validation-not-empty}")
        @Size(max = 500, message = "{validation-size-max}")
        String mapCode,
        @Size(max = 100, message = "{validation-size-max}")
        String seoTitle,
        @Size(max = 300, message = "{validation-size-max}")
        String seoDescription,
        @Size(max = 200, message = "{validation-size-max}")
        String seoKeywords
) {
}
