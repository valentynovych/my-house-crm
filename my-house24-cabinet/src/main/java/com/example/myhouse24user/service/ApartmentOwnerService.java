package com.example.myhouse24user.service;

import com.example.myhouse24user.model.authentication.RegistrationRequest;
import com.example.myhouse24user.model.owner.ViewOwnerResponse;

public interface ApartmentOwnerService {
    void register(RegistrationRequest registrationRequest);
    ViewOwnerResponse getViewOwnerResponse();
}
