package com.example.myhouse24user.validators.emailValidation.authentication;

import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailExistValidator implements ConstraintValidator<EmailExist,String> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;

    public EmailExistValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return apartmentOwnerRepo.existsApartmentOwnerByEmail(email);
    }
}
