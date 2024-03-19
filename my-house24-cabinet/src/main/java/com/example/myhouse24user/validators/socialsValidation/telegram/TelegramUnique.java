package com.example.myhouse24user.validators.socialsValidation.telegram;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
@Constraint(validatedBy = TelegramUniqueValidator.class)
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TelegramUnique {
    String telegramUsername();
    String id();
    String message() default "Ім'я користувача Telegram вже використовується";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        TelegramUnique[] value();
    }
}
