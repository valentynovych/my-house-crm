package com.example.myhouse24admin.validators.emailValidation.owners;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class OwnerEmailFieldUniqueValidator implements ConstraintValidator<OwnerEmailFieldUnique,String> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;

    public OwnerEmailFieldUniqueValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        Optional<ApartmentOwner> apartmentOwner = apartmentOwnerRepo.findByEmailAndDeletedIsFalse(email);
        return apartmentOwner.isEmpty();
    }
}
