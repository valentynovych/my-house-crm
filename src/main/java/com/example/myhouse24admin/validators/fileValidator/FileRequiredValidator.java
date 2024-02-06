package com.example.myhouse24admin.validators.fileValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileRequiredValidator implements ConstraintValidator<FileRequired, List<MultipartFile>> {
    @Override
    public boolean isValid(List<MultipartFile> multipartFiles, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
