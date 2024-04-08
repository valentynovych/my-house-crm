package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.model.authentication.EmailRequest;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {
    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private SendGrid sendGrid;
    @InjectMocks
    private MailServiceImpl mailService;
    @Test
    void sendToken_Should_Send_Token() throws IOException {
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("template");
        when(sendGrid.api(any(Request.class))).thenReturn(new Response());

        mailService.sendToken("token", new EmailRequest("email"), "cabinet/url");

        verify(templateEngine, times(1))
                .process(anyString(), any(Context.class));
        verify(sendGrid, times(1)).api(any(Request.class));

        verifyNoMoreInteractions(templateEngine);
        verifyNoMoreInteractions(sendGrid);
    }
    @Test
    void sendToken_Should_Throw_IOException() throws IOException {
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("template");
        doThrow(IOException.class).when(sendGrid).api(any(Request.class));

        mailService.sendToken("token", new EmailRequest("email"), "cabinet/url");

        verify(templateEngine, times(1))
                .process(anyString(), any(Context.class));
        verify(sendGrid, times(1)).api(any(Request.class));

        verifyNoMoreInteractions(templateEngine);
        verifyNoMoreInteractions(sendGrid);
    }
}