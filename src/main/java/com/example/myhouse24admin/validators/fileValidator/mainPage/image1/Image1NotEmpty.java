package com.example.myhouse24admin.validators.fileValidator.mainPage.image1;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
@Constraint(validatedBy = Image1NotEmptyValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Image1NotEmpty {
    String message() default "Зображення є обов'язковим";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
