package com.example.myhouse24admin.validators.fileValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImagesExtensionValidator implements ConstraintValidator<ImageExtension, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile multipartFiles, ConstraintValidatorContext constraintValidatorContext) {
        if (!multipartFiles.isEmpty()) {
            String contentType = multipartFiles.getContentType();
            if (contentType != null) {
                return contentType.equals("image/png")
                        || contentType.equals("image/jpg")
                        || contentType.equals("image/jpeg");
            }
        }
        return true;
    }
}
