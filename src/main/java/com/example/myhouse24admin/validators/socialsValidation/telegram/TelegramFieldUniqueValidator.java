package com.example.myhouse24admin.validators.socialsValidation.telegram;

import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import com.example.myhouse24admin.validators.socialsValidation.telegram.TelegramFieldUnique;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelegramFieldUniqueValidator implements ConstraintValidator<TelegramFieldUnique, String> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;

    public TelegramFieldUniqueValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(String telegramUsername, ConstraintValidatorContext constraintValidatorContext) {
        return telegramUsername.isEmpty()? true : !apartmentOwnerRepo.existsApartmentOwnerByTelegramUsername(telegramUsername);
    }
}
