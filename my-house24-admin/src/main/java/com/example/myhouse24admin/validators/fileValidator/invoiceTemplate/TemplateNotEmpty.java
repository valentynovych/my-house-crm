package com.example.myhouse24admin.validators.fileValidator.invoiceTemplate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = TemplateNotEmptyValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateNotEmpty {
    String message() default "Файл є обов'язковим";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
