package com.example.myhouse24admin.validators.phoneValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {PhoneOwnerValidator.class})
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneOwner {
    String phoneNumber() default "phoneNumber";
    String message() default "Номер вже використовується";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
