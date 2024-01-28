package com.example.myhouse24admin.validators.emailValidation;

import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.staff.StaffEditRequest;
import com.example.myhouse24admin.repository.StaffRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class StaffEmailOwnerValidator implements ConstraintValidator<StaffEmailOwner, StaffEditRequest> {

    private final StaffRepo staffRepo;

    public StaffEmailOwnerValidator(StaffRepo staffRepo) {
        this.staffRepo = staffRepo;
    }

    @Override
    public boolean isValid(StaffEditRequest staffEditRequest, ConstraintValidatorContext constraintValidatorContext) {
        Optional<Staff> byEmail = staffRepo.findByEmail(staffEditRequest.email());
        return byEmail.isEmpty() || byEmail.get().getId().equals(staffEditRequest.id());
    }
}
