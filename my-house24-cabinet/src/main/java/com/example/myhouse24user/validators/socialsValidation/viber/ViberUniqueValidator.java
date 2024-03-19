package com.example.myhouse24user.validators.socialsValidation.viber;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Optional;

public class ViberUniqueValidator implements ConstraintValidator<ViberUnique, Object> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private String viberNumber;
    private String id;

    public ViberUniqueValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        String viberNumberValue = (String) new BeanWrapperImpl(o).getPropertyValue(viberNumber);
        if(viberNumberValue.isEmpty()){
            return true;
        }
        Long idValue = (Long) new BeanWrapperImpl(o).getPropertyValue(id);
        Optional<ApartmentOwner> apartmentOwner = apartmentOwnerRepo.findByViberNumberAndDeletedIsFalse(viberNumberValue);
        if(apartmentOwner.isPresent() && !apartmentOwner.get().getId().equals(idValue))
            return false;
        return true;
    }

    @Override
    public void initialize(ViberUnique constraintAnnotation) {
        this.id = constraintAnnotation.id();
        this.viberNumber = constraintAnnotation.viberNumber();
    }
}
