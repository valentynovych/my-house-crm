package com.example.myhouse24user.validators.policyValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
@Constraint(validatedBy = PolicyTrueValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PolicyTrue {
    String message() default "Прийміть політику конфідеційності";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
