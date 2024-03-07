package com.example.myhouse24admin.validators.phoneValidation;

import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.staff.StaffEditRequest;
import com.example.myhouse24admin.repository.StaffRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class PhoneOwnerValidator  implements ConstraintValidator<PhoneOwner, StaffEditRequest> {

    private final StaffRepo staffRepo;

    public PhoneOwnerValidator(StaffRepo staffRepo) {
        this.staffRepo = staffRepo;
    }

    @Override
    public boolean isValid(StaffEditRequest staffEditRequest, ConstraintValidatorContext constraintValidatorContext) {
        Optional<Staff> byPhoneNumber = staffRepo.findByPhoneNumber(staffEditRequest.phoneNumber());
        return byPhoneNumber.isEmpty() || byPhoneNumber.get().getId().equals(staffEditRequest.id());
    }
}
