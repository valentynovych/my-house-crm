package com.example.myhouse24admin.service;

import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.authentication.EmailRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface MailService {
    void sendToken(String token, EmailRequest emailRequest, HttpServletRequest httpRequest);

    void sendNewPassword(String to, String newPassword);

    void sendMessage(String to, String subject, String messageHtml, Staff staff, HttpServletRequest request);
}
