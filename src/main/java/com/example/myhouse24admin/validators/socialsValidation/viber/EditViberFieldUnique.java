package com.example.myhouse24admin.validators.socialsValidation.viber;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
@Constraint(validatedBy = EditViberFieldUniqueValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EditViberFieldUnique {
    String message() default "Номер телефону для Viber вже використовується";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
