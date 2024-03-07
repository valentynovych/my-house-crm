package com.example.myhouse24admin.validators.fileValidator.mainPage;

import com.example.myhouse24admin.entity.MainPageBlock;
import com.example.myhouse24admin.repository.MainPageBlockRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public class MainBlockImageNotEmptyValidator implements ConstraintValidator<MainBlockImageNotEmpty, Object> {
    private String image;
    private String id;
    private final MainPageBlockRepo mainPageBlockRepo;

    public MainBlockImageNotEmptyValidator(MainPageBlockRepo mainPageBlockRepo) {
        this.mainPageBlockRepo = mainPageBlockRepo;
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        Long idValue = (Long) new BeanWrapperImpl(o).getPropertyValue(id);
        MultipartFile imageValue = (MultipartFile) new BeanWrapperImpl(o).getPropertyValue(image);
        Optional<MainPageBlock> mainPageBlock = mainPageBlockRepo.findById(idValue);
        if(mainPageBlock.isEmpty() && imageValue.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void initialize(MainBlockImageNotEmpty constraintAnnotation) {
        this.image = constraintAnnotation.image();
        this.id = constraintAnnotation.id();
    }
}
