package com.example.myhouse24admin.validators.personalAccountValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {ValidatePersonalAccountUpdateRequestValidator.class})
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatePersonalAccountUpdateRequest {

    String message() default "{validation-apartment-personal-account-number-exists}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
