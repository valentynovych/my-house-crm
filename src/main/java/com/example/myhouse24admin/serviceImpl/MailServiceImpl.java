package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.model.authentication.EmailRequest;
import com.example.myhouse24admin.service.MailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
public class MailServiceImpl implements MailService {
    private final TemplateEngine templateEngine;
    private final SendGrid sendGrid;
    private final Logger logger = LogManager.getLogger("serviceLogger");

    @Value("${sender}")
    private String sender;

    public MailServiceImpl(TemplateEngine templateEngine, SendGrid sendGrid) {
        this.templateEngine = templateEngine;
        this.sendGrid = sendGrid;
    }

    @Async

    @Override
    public void sendToken(String token, EmailRequest emailRequest, HttpServletRequest httpRequest) {
        logger.info("sendToken() - Sending token "+token+" to email "+emailRequest.email());
        Email from = new Email(sender);
        String subject = "Встановлення нового паролю";
        Email toEmail = new Email(emailRequest.email());
        Content content = new Content("text/html", buildContent(token,httpRequest));
        Mail mail = new Mail(from, subject, toEmail, content);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sendGrid.api(request);
            logger.info("sendToken() - Token was sent");
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
    private String buildContent(String token, HttpServletRequest httpRequest) {
        String link = formLink(token, httpRequest);
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process("email/passwordResetTokenEmailTemplate", context);
    }
    private String formLink(String token, HttpServletRequest httpRequest){
        String fullUrl = ServletUriComponentsBuilder.fromRequestUri(httpRequest).build().toUriString();
        int index = fullUrl.lastIndexOf("/");
        String baseUrl = fullUrl.substring(0, index);
        String link = baseUrl +"/changePassword?token="+token;
        return link;
    }
}
