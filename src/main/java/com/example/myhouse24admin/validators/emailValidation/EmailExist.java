package com.example.myhouse24admin.validators.emailValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Constraint(validatedBy = EmailExistValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailExist {
    String message() default "Невірна пошта";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
