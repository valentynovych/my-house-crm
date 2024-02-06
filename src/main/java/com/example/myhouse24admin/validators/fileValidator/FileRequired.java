package com.example.myhouse24admin.validators.fileValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FileRequiredValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileRequired {

    String message() default "{validation-file-required}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
