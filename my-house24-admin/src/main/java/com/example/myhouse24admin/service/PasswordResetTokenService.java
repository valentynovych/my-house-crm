package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.authentication.EmailRequest;

public interface PasswordResetTokenService {
    String createOrUpdatePasswordResetToken(EmailRequest emailRequest);
    boolean isPasswordResetTokenValid(String token);
    void updatePassword(String token, String password);

}
