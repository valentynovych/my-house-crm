package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.apartmentOwner.CreateApartmentOwnerRequest;
import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerResponse;
import com.example.myhouse24admin.model.apartmentOwner.EditApartmentOwnerRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ApartmentOwnerService {
    void createApartmentOwner(CreateApartmentOwnerRequest createApartmentOwnerRequest, MultipartFile avatar);
    ApartmentOwnerResponse getApartmentOwnerResponse(Long id);
    void updateApartmentOwner(EditApartmentOwnerRequest editApartmentOwnerRequest, Long id, MultipartFile multipartFile);
}
