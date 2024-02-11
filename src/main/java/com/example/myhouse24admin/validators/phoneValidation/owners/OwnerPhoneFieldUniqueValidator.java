package com.example.myhouse24admin.validators.phoneValidation.owners;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class OwnerPhoneFieldUniqueValidator implements ConstraintValidator<OwnerPhoneFieldUnique, String> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;

    public OwnerPhoneFieldUniqueValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        Optional<ApartmentOwner> apartmentOwner = apartmentOwnerRepo.findByPhoneNumberAndDeletedIsFalse(phoneNumber);
        return apartmentOwner.isEmpty();
    }
}
