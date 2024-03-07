package com.example.myhouse24admin.validators.socialsValidation.viber;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class ViberFieldUniqueValidator implements ConstraintValidator<ViberFieldUnique, String> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;

    public ViberFieldUniqueValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(String viberNumber, ConstraintValidatorContext constraintValidatorContext) {
        if(viberNumber.isEmpty()) return true;
        Optional<ApartmentOwner> apartmentOwner = apartmentOwnerRepo.findByViberNumberAndDeletedIsFalse(viberNumber);
        return apartmentOwner.isEmpty();
    }
}
