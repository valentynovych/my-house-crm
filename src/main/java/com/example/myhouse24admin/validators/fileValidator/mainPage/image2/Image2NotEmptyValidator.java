package com.example.myhouse24admin.validators.fileValidator.mainPage.image2;

import com.example.myhouse24admin.entity.MainPage;
import com.example.myhouse24admin.repository.MainPageRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class Image2NotEmptyValidator implements ConstraintValidator<Image2NotEmpty, MultipartFile> {
    private final MainPageRepo mainPageRepo;

    public Image2NotEmptyValidator(MainPageRepo mainPageRepo) {
        this.mainPageRepo = mainPageRepo;
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        MainPage mainPage = mainPageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("Main page was not found by id 1"));
        if(multipartFile.isEmpty() && (mainPage.getImage2() == null || mainPage.getImage2().isEmpty())) {
            return false;
        }
        return true;
    }
}
