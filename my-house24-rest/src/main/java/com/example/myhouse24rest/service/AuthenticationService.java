package com.example.myhouse24rest.service;

import com.example.myhouse24rest.model.auth.AuthRequest;
import com.example.myhouse24rest.model.auth.JwtResponse;
import com.example.myhouse24rest.model.auth.RefreshTokenRequest;

public interface AuthenticationService {
    JwtResponse login(AuthRequest loginRequest);

    JwtResponse refreshToken(RefreshTokenRequest refreshToken);
}
