package com.example.myhouse24admin.validators.fileValidator.invoiceTemplate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class TemplateNotEmptyValidator implements ConstraintValidator<TemplateNotEmpty, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        return !multipartFile.isEmpty();
    }
}
