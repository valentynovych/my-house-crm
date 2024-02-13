package com.example.myhouse24admin.validators.apartmentValidation;

import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.repository.ApartmentRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredNewOrPresentAccountValidator implements ConstraintValidator<RequiredNewOrPresentAccount, ApartmentAddRequest> {

    private final ApartmentRepo apartmentRepo;

    public RequiredNewOrPresentAccountValidator(ApartmentRepo apartmentRepo) {
        this.apartmentRepo = apartmentRepo;
    }

    @Override
    public boolean isValid(ApartmentAddRequest request, ConstraintValidatorContext constraintValidatorContext) {
        // todo add valid present or new personal account
        return true;
    }
}
