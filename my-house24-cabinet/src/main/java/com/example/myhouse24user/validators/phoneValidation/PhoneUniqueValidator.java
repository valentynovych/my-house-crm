package com.example.myhouse24user.validators.phoneValidation;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Optional;

public class PhoneUniqueValidator implements ConstraintValidator<PhoneUnique, Object> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private String phoneNumber;
    private String id;

    public PhoneUniqueValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        String phoneNumberValue = (String) new BeanWrapperImpl(o).getPropertyValue(phoneNumber);
        Long idValue = (Long) new BeanWrapperImpl(o).getPropertyValue(id);
        Optional<ApartmentOwner> apartmentOwner = apartmentOwnerRepo.findByPhoneNumberAndDeletedIsFalse(phoneNumberValue);
        if(apartmentOwner.isPresent() && !apartmentOwner.get().getId().equals(idValue))
            return false;
        return true;
    }

    @Override
    public void initialize(PhoneUnique constraintAnnotation) {
        this.id = constraintAnnotation.id();
        this.phoneNumber = constraintAnnotation.phoneNumber();
    }
}
