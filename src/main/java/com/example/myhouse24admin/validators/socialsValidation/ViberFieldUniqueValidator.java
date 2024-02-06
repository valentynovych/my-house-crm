package com.example.myhouse24admin.validators.socialsValidation;

import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ViberFieldUniqueValidator implements ConstraintValidator<ViberFieldUnique, String> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;

    public ViberFieldUniqueValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(String viberNumber, ConstraintValidatorContext constraintValidatorContext) {
        return viberNumber.isEmpty()? true : !apartmentOwnerRepo.existsApartmentOwnerByViberNumber(viberNumber);
    }
}
