package com.example.myhouse24admin.service;

import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.apartmentOwner.InvitationRequest;
import com.example.myhouse24admin.model.authentication.EmailRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface MailService {
    void sendToken(String token, EmailRequest emailRequest, String currentUrl);

    void sendNewPassword(String to, String newPassword);

    void sendMessage(String to, String subject, String messageHtml, Staff staff);

    void sendInviteToStaff(String token,Staff staffById, String currentUrl);
    void sendActivationToOwner(String token, Long ownerId);
    void sendInvitationToOwner(InvitationRequest invitationRequest);
    void sendInvoice(String to, byte[] fileBytes);
}
