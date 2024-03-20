package com.example.myhouse24user.validators.emailValidation.owner;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = EmailUniqueValidator.class)
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailUnique {
    String email();
    String id();
    String message() default "Пошта вже використовується";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        EmailUnique[] value();
    }
}
