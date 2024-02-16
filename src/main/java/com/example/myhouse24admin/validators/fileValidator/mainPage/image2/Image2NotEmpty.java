package com.example.myhouse24admin.validators.fileValidator.mainPage.image2;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
@Constraint(validatedBy = Image2NotEmptyValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Image2NotEmpty {
    String message() default "Зображення є обов'язковим";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
