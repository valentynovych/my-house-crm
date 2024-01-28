package com.example.myhouse24admin.validators.passwordValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch,Object> {
    private String password;
    private String confirmPassword;
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        String passwordValue = (String) new BeanWrapperImpl(o).getPropertyValue(password);
        String confirmPasswordValue = (String) new BeanWrapperImpl(o).getPropertyValue(confirmPassword);
        return passwordValue.equals(confirmPasswordValue);
    }

    @Override
    public void initialize(PasswordsMatch passwordsMatch) {
        this.password = passwordsMatch.password();
        this.confirmPassword = passwordsMatch.confirmPassword();
    }
}
