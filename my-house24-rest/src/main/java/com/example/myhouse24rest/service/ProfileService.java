package com.example.myhouse24rest.service;

import com.example.myhouse24rest.model.profile.ProfileResponse;

import java.security.Principal;

public interface ProfileService {
    ProfileResponse getProfile(Principal principal);
}
