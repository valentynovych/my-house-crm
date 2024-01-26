package com.example.myhouse24admin.validators.phoneValidation;

import com.example.myhouse24admin.repository.StaffRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneExistValidator implements ConstraintValidator<PhoneExist, String> {

    private final StaffRepo staffRepo;

    public PhoneExistValidator(StaffRepo staffRepo) {
        this.staffRepo = staffRepo;
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        return !staffRepo.existsStaffByPhoneNumber(phoneNumber);
    }
}
