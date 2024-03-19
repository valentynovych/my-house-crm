package com.example.myhouse24user.service;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.model.authentication.RegistrationRequest;
import com.example.myhouse24user.model.owner.ViewOwnerResponse;

public interface ApartmentOwnerService {
    void register(RegistrationRequest registrationRequest);

    ApartmentOwner findApartmentOwnerByEmail(String ownerEmail);
    ViewOwnerResponse getViewOwnerResponse();
}
