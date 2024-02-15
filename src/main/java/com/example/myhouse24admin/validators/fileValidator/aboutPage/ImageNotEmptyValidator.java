package com.example.myhouse24admin.validators.fileValidator.aboutPage;

import com.example.myhouse24admin.entity.AboutPage;
import com.example.myhouse24admin.repository.AboutPageRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.multipart.MultipartFile;

public class ImageNotEmptyValidator implements ConstraintValidator<ImageNotEmpty, MultipartFile> {
    private final AboutPageRepo aboutPageRepo;

    public ImageNotEmptyValidator(AboutPageRepo aboutPageRepo) {
        this.aboutPageRepo = aboutPageRepo;
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        AboutPage aboutPage = aboutPageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("About page was not found by id 1"));
        if (multipartFile.isEmpty() && (aboutPage.getDirectorImage() == null || aboutPage.getDirectorImage().isEmpty())){
            return false;
        }
        return true;
    }
}
