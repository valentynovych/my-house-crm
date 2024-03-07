package com.example.myhouse24admin.validators.fileValidator.mainPage.image3;

import com.example.myhouse24admin.entity.MainPage;
import com.example.myhouse24admin.repository.MainPageRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class Image3NotEmptyValidator implements ConstraintValidator<Image3NotEmpty, MultipartFile> {
    private final MainPageRepo mainPageRepo;

    public Image3NotEmptyValidator(MainPageRepo mainPageRepo) {
        this.mainPageRepo = mainPageRepo;
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        MainPage mainPage = mainPageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("Main page was not found by id 1"));
        if(multipartFile.isEmpty() && (mainPage.getImage3() == null || mainPage.getImage3().isEmpty())) {
            return false;
        }
        return true;
    }
}
