package com.example.myhouse24admin.validators.socialsValidation.telegram;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
@Constraint(validatedBy = TelegramFieldUniqueValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TelegramFieldUnique {
    String message() default "Ім'я користувача Telegram вже використовується";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
