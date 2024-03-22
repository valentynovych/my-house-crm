package com.example.myhouse24rest.service;

import com.example.myhouse24rest.model.auth.AuthRequest;
import com.example.myhouse24rest.model.auth.JwtResponse;

public interface AuthenticationService {
    JwtResponse login(AuthRequest loginRequest);
}
