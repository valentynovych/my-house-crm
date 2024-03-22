package com.example.myhouse24user.service;

import com.example.myhouse24user.model.authentication.EmailRequest;

public interface MailService {
    void sendToken(String token, EmailRequest emailRequest, String currentUrl);
}
