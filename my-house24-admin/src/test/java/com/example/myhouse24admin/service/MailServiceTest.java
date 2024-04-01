package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.authentication.EmailRequest;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import com.example.myhouse24admin.serviceImpl.MailServiceImpl;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class MailServiceTest {
    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private SendGrid sendGrid;
    @Mock
    private ApartmentOwnerRepo apartmentOwnerRepo;
    @InjectMocks
    private MailServiceImpl mailService;
    @Test
    void sendToken_Should_Send_Token() throws IOException {
        when(templateEngine.process(anyString(), any())).thenReturn("template");
        when(sendGrid.api(any(Request.class))).thenReturn(new Response());

        mailService.sendToken("token", new EmailRequest("email"), "/admin/url");
    }
    @Test
    void sendToken_Should_Throw_IOException() throws IOException {
        when(templateEngine.process(anyString(), any())).thenReturn("template");
        doThrow(IOException.class).when(sendGrid).api(any(Request.class));

        mailService.sendToken("token", new EmailRequest("email"), "/admin/url");
    }

    @Test
    void sendNewPassword() {
    }

    @Test
    void sendMessage() {
    }

    @Test
    void sendInviteToStaff() {
    }

    @Test
    void sendActivationToOwner() {
    }

    @Test
    void sendInvitationToOwner() {
    }

    @Test
    void sendInvoice() {
    }
}