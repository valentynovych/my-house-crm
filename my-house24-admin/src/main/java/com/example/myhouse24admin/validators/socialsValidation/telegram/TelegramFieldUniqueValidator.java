package com.example.myhouse24admin.validators.socialsValidation.telegram;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class TelegramFieldUniqueValidator implements ConstraintValidator<TelegramFieldUnique, String> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;

    public TelegramFieldUniqueValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(String telegramUsername, ConstraintValidatorContext constraintValidatorContext) {
        if(telegramUsername.isEmpty()) return true;
        Optional<ApartmentOwner> apartmentOwner = apartmentOwnerRepo.findByTelegramUsernameAndDeletedIsFalse(telegramUsername);
        return apartmentOwner.isEmpty();
    }
}
