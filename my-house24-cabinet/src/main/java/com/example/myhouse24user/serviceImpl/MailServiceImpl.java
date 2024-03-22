package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.model.authentication.EmailRequest;
import com.example.myhouse24user.service.MailService;
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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
@Service
public class MailServiceImpl implements MailService {
    private final TemplateEngine templateEngine;
    private final SendGrid sendGrid;
    private final Logger logger = LogManager.getLogger(MailServiceImpl.class);

    @Value("${sender}")
    private String sender;

    public MailServiceImpl(TemplateEngine templateEngine, SendGrid sendGrid) {
        this.templateEngine = templateEngine;
        this.sendGrid = sendGrid;
    }

    @Async
    @Override
    public void sendToken(String token, EmailRequest emailRequest, String currentUrl) {
        logger.info("sendToken() - Sending token " + token + " to email " + emailRequest.email());
        String subject = "Встановлення нового паролю";
        Content content = new Content("text/html", buildContent(token, currentUrl));
        sendMail(subject, emailRequest.email(), content);
        logger.info("sendToken() - Token was sent");
    }
    private String buildContent(String token, String currentUrl) {
        String link = formLink(token, currentUrl);
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process("email/passwordResetTokenEmailTemplate", context);
    }
    private String formLink(String token, String currentUrl) {
        int index = currentUrl.indexOf("cabinet");
        String baseUrl = currentUrl.substring(0, index);
        String link = baseUrl + "cabinet/changePassword?token=" + token;
        return link;
    }
    private void sendMail(String subject, String to, Content content) {
        logger.info("sendMail() - start");
        Mail mail = new Mail(getEmailFromAddress(sender), subject, getEmailFromAddress(to), content);

        try {
            Request request = buildRequest(mail);
            logger.info("sendMail() - start sending mail to: {}", to);
            sendGrid.api(request);
            logger.info("sendMail() - Mail has been send");
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
    private Request buildRequest(Mail mail) throws IOException {
        logger.info("buildRequest() -> Start");
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        logger.info("buildRequest() -> End, success build");
        return request;
    }

    private Email getEmailFromAddress(String address) {
        return new Email(address);
    }
}
