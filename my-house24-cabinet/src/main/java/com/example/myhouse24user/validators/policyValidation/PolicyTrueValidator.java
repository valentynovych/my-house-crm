package com.example.myhouse24user.validators.policyValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PolicyTrueValidator implements ConstraintValidator<PolicyTrue, Boolean> {
    @Override
    public boolean isValid(Boolean policy, ConstraintValidatorContext constraintValidatorContext) {
        return policy;
    }
}
