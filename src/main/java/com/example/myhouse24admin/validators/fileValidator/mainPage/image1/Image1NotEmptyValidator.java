package com.example.myhouse24admin.validators.fileValidator.mainPage.image1;

import com.example.myhouse24admin.entity.MainPage;
import com.example.myhouse24admin.repository.MainPageRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class Image1NotEmptyValidator implements ConstraintValidator<Image1NotEmpty, MultipartFile> {
    private final MainPageRepo mainPageRepo;

    public Image1NotEmptyValidator(MainPageRepo mainPageRepo) {
        this.mainPageRepo = mainPageRepo;
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        MainPage mainPage = mainPageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("Main page was not found by id 1"));
        if(multipartFile.isEmpty() && (mainPage.getImage1() == null || mainPage.getImage1().isEmpty())) {
            return false;
        }
        return true;
    }
}
