package com.example.myhouse24admin.validators.personalAccountValidation;

import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountUpdateRequest;
import com.example.myhouse24admin.repository.PersonalAccountRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class ValidatePersonalAccountUpdateRequestValidator
        implements ConstraintValidator<ValidatePersonalAccountUpdateRequest, PersonalAccountUpdateRequest> {

    private final PersonalAccountRepo personalAccountRepo;

    public ValidatePersonalAccountUpdateRequestValidator(PersonalAccountRepo personalAccountRepo) {
        this.personalAccountRepo = personalAccountRepo;
    }

    @Override
    public boolean isValid(PersonalAccountUpdateRequest request, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean isValid = true;
        if (request.getAccountNumber() != null) {
            Optional<PersonalAccount> accountByAccountNumber =
                    personalAccountRepo.findPersonalAccountByAccountNumber(request.getAccountNumber());
            context.buildConstraintViolationWithTemplate("{validation-apartment-personal-account-number-exists}")
                    .addPropertyNode("accountNumber")
                    .addBeanNode()
                    .addConstraintViolation();
            isValid = accountByAccountNumber.isEmpty() || request.getId().equals(accountByAccountNumber.get().getId());
        } else {
            context.buildConstraintViolationWithTemplate("{validation-field-required}")
                    .addPropertyNode("accountNumber")
                    .addBeanNode()
                    .addConstraintViolation();
            isValid = false;
        }
        if (request.getHouseId() != null) {

            if (request.getSectionId() == null) {
                context.buildConstraintViolationWithTemplate("{validation-field-required}")
                        .addPropertyNode("sectionId")
                        .addBeanNode()
                        .addConstraintViolation();
                isValid = false;
            }
            if (request.getApartmentId() == null) {
                context.buildConstraintViolationWithTemplate("{validation-field-required}")
                        .addPropertyNode("apartmentId")
                        .addBeanNode()
                        .addConstraintViolation();
                isValid = false;
            }
        }
        return isValid;
    }
}
