package com.example.myhouse24user.service;


import com.example.myhouse24user.model.authentication.EmailRequest;

public interface OwnerPasswordResetTokenService {
    String createOrUpdatePasswordResetToken(EmailRequest emailRequest);
    boolean isPasswordResetTokenValid(String token);
    void updatePassword(String token, String password);

}
