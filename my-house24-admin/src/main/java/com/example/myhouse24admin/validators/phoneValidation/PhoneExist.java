package com.example.myhouse24admin.validators.phoneValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = PhoneExistValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneExist {
    String message() default "Телефон уже використовується";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
