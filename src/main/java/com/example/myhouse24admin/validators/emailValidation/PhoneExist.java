package com.example.myhouse24admin.validators.emailValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Constraint(validatedBy = PhoneExistValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneExist {
    String message() default "Телефон уже використовується";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
