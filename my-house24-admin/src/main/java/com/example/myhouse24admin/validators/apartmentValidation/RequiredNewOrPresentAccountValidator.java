package com.example.myhouse24admin.validators.apartmentValidation;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.PersonalAccountRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class RequiredNewOrPresentAccountValidator implements ConstraintValidator<RequiredNewOrPresentAccount, ApartmentAddRequest> {

    private final ApartmentRepo apartmentRepo;
    private final PersonalAccountRepo personalAccountRepo;

    public RequiredNewOrPresentAccountValidator(ApartmentRepo apartmentRepo, PersonalAccountRepo personalAccountRepo) {
        this.apartmentRepo = apartmentRepo;
        this.personalAccountRepo = personalAccountRepo;
    }

    @Override
    public void initialize(RequiredNewOrPresentAccount constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ApartmentAddRequest request, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if (request.getPersonalAccountNew() != null && request.getPersonalAccountNew() > 0) {

            boolean isExists = personalAccountRepo.existsPersonalAccountByAccountNumber(request.getPersonalAccountNew());
            context.buildConstraintViolationWithTemplate("{validation-apartment-personal-account-number-exists}")
                    .addPropertyNode("personalAccountNew")
                    .addBeanNode()
                    .addConstraintViolation();
            return !isExists;
        }

        if (request.getPersonalAccountId() != null) {
            context.disableDefaultConstraintViolation();
            Optional<Apartment> apartmentByPersonalAccountId =
                    apartmentRepo.findApartmentByPersonalAccount_Id(request.getPersonalAccountId());
            context.buildConstraintViolationWithTemplate("{validation-apartment-personal-account-id-exists}")
                    .addPropertyNode("personalAccountId")
                    .addBeanNode()
                    .addConstraintViolation();
            return apartmentByPersonalAccountId.isEmpty()
                    || (request.getId() != null && request.getId().equals(apartmentByPersonalAccountId.get().getId()));
        }
        context.buildConstraintViolationWithTemplate("{validation-field-required}")
                .addPropertyNode("personalAccountNew")
                .addBeanNode()
                .addConstraintViolation();
        return false;
    }
}
