package com.example.myhouse24admin.validators.emailValidation.owners;

import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OwnerEmailFieldUniqueValidator implements ConstraintValidator<OwnerEmailFieldUnique,String> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;

    public OwnerEmailFieldUniqueValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return !apartmentOwnerRepo.existsApartmentOwnerByEmail(email);
    }
}
