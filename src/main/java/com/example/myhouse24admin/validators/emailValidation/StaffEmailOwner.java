package com.example.myhouse24admin.validators.emailValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {StaffEmailOwnerValidator.class})
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StaffEmailOwner {
    String email() default "email";
    String message() default "{validation-email-exist}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
