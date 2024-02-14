package com.example.myhouse24admin.validators.fileValidator.servicesPage;

import com.example.myhouse24admin.entity.ServicePageBlock;
import com.example.myhouse24admin.repository.ServicePageBlockRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public class ImageNotEmptyValidator implements ConstraintValidator<ImageNotEmpty, Object> {
    private String image;
    private String id;
    private final ServicePageBlockRepo servicePageBlockRepo;

    public ImageNotEmptyValidator(ServicePageBlockRepo servicePageBlockRepo) {
        this.servicePageBlockRepo = servicePageBlockRepo;
    }

    @Override
    public void initialize(ImageNotEmpty constraintAnnotation) {
        this.image = constraintAnnotation.image();
        this.id = constraintAnnotation.id();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        Long idValue = (Long) new BeanWrapperImpl(o).getPropertyValue(id);
        MultipartFile imageValue = (MultipartFile) new BeanWrapperImpl(o).getPropertyValue(image);
        Optional<ServicePageBlock> servicePageBlock = servicePageBlockRepo.findById(idValue);
        if(servicePageBlock.isEmpty() && imageValue.isEmpty()){
            return false;
        }
        return true;
    }
}
