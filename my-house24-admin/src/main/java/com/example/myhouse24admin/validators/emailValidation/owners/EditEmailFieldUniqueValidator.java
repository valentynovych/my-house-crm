package com.example.myhouse24admin.validators.emailValidation.owners;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Optional;

public class EditEmailFieldUniqueValidator implements ConstraintValidator<EditEmailFieldUnique, String> {

    private final HttpServletRequest httpServletRequest;
    private final ApartmentOwnerRepo apartmentOwnerRepo;

    public EditEmailFieldUniqueValidator(HttpServletRequest httpServletRequest, ApartmentOwnerRepo apartmentOwnerRepo) {
        this.httpServletRequest = httpServletRequest;
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        Object object =  httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Long> map = objectMapper
                .convertValue(object, new TypeReference<Map<String, Long>>() {});
        Optional<ApartmentOwner> apartmentOwner = apartmentOwnerRepo.findByEmailAndDeletedIsFalse(email);
        if(apartmentOwner.isPresent() && !apartmentOwner.get().getId().equals(map.get("id"))) return false;
        return true;
    }
}
