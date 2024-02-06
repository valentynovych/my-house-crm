package com.example.myhouse24admin.validators.phoneValidation.owners;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
@Constraint(validatedBy = OwnerPhoneFieldUniqueValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OwnerPhoneFieldUnique {
    String message() default "Номер телефону вже використовується";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
