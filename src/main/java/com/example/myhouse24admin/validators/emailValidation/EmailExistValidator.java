package com.example.myhouse24admin.validators.emailValidation;

import com.example.myhouse24admin.repository.StaffRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailExistValidator implements ConstraintValidator<EmailExist,String> {
    private final StaffRepo staffRepo;

    public EmailExistValidator(StaffRepo staffRepo) {
        this.staffRepo = staffRepo;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return staffRepo.existsStaffByEmail(email);
    }
}
