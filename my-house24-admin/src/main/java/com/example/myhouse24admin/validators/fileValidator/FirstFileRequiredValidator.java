package com.example.myhouse24admin.validators.fileValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FirstFileRequiredValidator implements ConstraintValidator<FirstFileRequired, List<MultipartFile>> {

    @Override
    public boolean isValid(List<MultipartFile> multipartFiles, ConstraintValidatorContext constraintValidatorContext) {
        if (!multipartFiles.isEmpty()) {
            MultipartFile file = multipartFiles.get(0);
            return !file.isEmpty();
        }
        return true;
    }
}
