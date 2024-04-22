package com.example.myhouse24admin.validators.personalAccountValidation;

import com.example.myhouse24admin.repository.PersonalAccountRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniquePersonalAccountNumberValidator implements ConstraintValidator<UniquePersonalAccountNumber, String> {

    private final PersonalAccountRepo personalAccountRepo;

    public UniquePersonalAccountNumberValidator(PersonalAccountRepo personalAccountRepo) {
        this.personalAccountRepo = personalAccountRepo;
    }

    @Override
    public boolean isValid(String accountNumber, ConstraintValidatorContext constraintValidatorContext) {
        return !personalAccountRepo.existsPersonalAccountByAccountNumber(accountNumber);
    }
}
