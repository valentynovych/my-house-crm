package com.example.myhouse24admin.validators.personalAccountValidation;

import com.example.myhouse24admin.model.personalAccounts.PersonalAccountAddRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotRequiredApartmentInPersonalAccountValidator
        implements ConstraintValidator<NotRequiredApartmentInPersonalAccount, PersonalAccountAddRequest> {

    @Override
    public boolean isValid(PersonalAccountAddRequest request, ConstraintValidatorContext context) {
        boolean isValid = true;
        if (request.getHouseId() != null) {
            context.disableDefaultConstraintViolation();
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
