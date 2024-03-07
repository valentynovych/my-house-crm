package com.example.myhouse24admin.validators.apartmentValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {RequiredNewOrPresentAccountValidator.class})
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredNewOrPresentAccount {
    String message() default "{validation-email-exist}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
