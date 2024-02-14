package com.example.myhouse24admin.validators.fileValidator.servicesPage;

import com.example.myhouse24admin.validators.passwordValidation.PasswordsMatch;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = ImageNotEmptyValidator.class)
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageNotEmpty {
    String image();
    String id();
    String message() default "Зображення є обов'язковим";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ImageNotEmpty[] value();
    }
}
