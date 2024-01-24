package com.example.myhouse24admin.validators.emailValidation;

import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.repository.StaffRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class EmailExistValidator implements ConstraintValidator<EmailExist,String> {
    private final StaffRepo staffRepo;

    public EmailExistValidator(StaffRepo staffRepo) {
        this.staffRepo = staffRepo;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        Optional<Staff> staff = staffRepo.findByEmail(email);
        return staff.isPresent();
    }
}
