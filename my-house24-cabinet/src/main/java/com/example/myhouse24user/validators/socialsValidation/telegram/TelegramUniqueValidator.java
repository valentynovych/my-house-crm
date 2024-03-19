package com.example.myhouse24user.validators.socialsValidation.telegram;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Optional;

public class TelegramUniqueValidator implements ConstraintValidator<TelegramUnique, Object> {
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private String id;
    private String telegramUsername;

    public TelegramUniqueValidator(ApartmentOwnerRepo apartmentOwnerRepo) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        String telegramUsernameValue = (String) new BeanWrapperImpl(o).getPropertyValue(telegramUsername);
        if(telegramUsernameValue.isEmpty()){
            return true;
        }
        Long idValue = (Long) new BeanWrapperImpl(o).getPropertyValue(id);
        Optional<ApartmentOwner> apartmentOwner = apartmentOwnerRepo.findByTelegramUsernameAndDeletedIsFalse(telegramUsernameValue);
        if(apartmentOwner.isPresent() && !apartmentOwner.get().getId().equals(idValue))
            return false;
        return true;
    }

    @Override
    public void initialize(TelegramUnique constraintAnnotation) {
        this.id = constraintAnnotation.id();
        this.telegramUsername = constraintAnnotation.telegramUsername();
    }
}
