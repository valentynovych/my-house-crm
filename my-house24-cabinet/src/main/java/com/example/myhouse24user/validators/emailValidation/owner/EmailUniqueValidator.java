package com.example.myhouse24user.validators.emailValidation.owner;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Optional;

public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, Object> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private String id;
    private String email;

    public EmailUniqueValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        String emailValue = (String) new BeanWrapperImpl(o).getPropertyValue(email);
        Long idValue = (Long) new BeanWrapperImpl(o).getPropertyValue(id);
        Optional<ApartmentOwner> apartmentOwner = apartmentOwnerRepo.findByEmailAndDeletedIsFalse(emailValue);
        if(apartmentOwner.isPresent() && !apartmentOwner.get().getId().equals(idValue))
            return false;
        return true;
    }

    @Override
    public void initialize(EmailUnique constraintAnnotation) {
        this.id = constraintAnnotation.id();
        this.email = constraintAnnotation.email();
    }
}
