package com.example.myhouse24user.service;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.model.authentication.RegistrationRequest;
import com.example.myhouse24user.model.owner.ApartmentOwnerRequest;
import com.example.myhouse24user.model.owner.EditOwnerResponse;
import com.example.myhouse24user.model.owner.ViewOwnerResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ApartmentOwnerService {
    void register(RegistrationRequest registrationRequest);

    ApartmentOwner findApartmentOwnerByEmail(String ownerEmail);
    ViewOwnerResponse getViewOwnerResponse();
    EditOwnerResponse getEditOwnerResponse();
    void updateProfile(ApartmentOwnerRequest apartmentOwnerRequest, MultipartFile multipartFile);
}
