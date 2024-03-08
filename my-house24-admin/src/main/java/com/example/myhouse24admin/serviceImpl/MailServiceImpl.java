package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.apartmentOwner.InvitationRequest;
import com.example.myhouse24admin.model.authentication.EmailRequest;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import com.example.myhouse24admin.service.MailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.persistence.EntityNotFoundException;
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
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private final HttpServletRequest httpServletRequest;
    private final Logger logger = LogManager.getLogger(MailServiceImpl.class);

    @Value("${sender}")
    private String sender;

    public MailServiceImpl(TemplateEngine templateEngine,
                           SendGrid sendGrid,
                           ApartmentOwnerRepo apartmentOwnerRepo,
                           HttpServletRequest httpServletRequest) {
        this.templateEngine = templateEngine;
        this.sendGrid = sendGrid;
        this.apartmentOwnerRepo = apartmentOwnerRepo;
        this.httpServletRequest = httpServletRequest;
    }

    @Async
    @Override
    public void sendToken(String token, EmailRequest emailRequest) {
        logger.info("sendToken() - Sending token " + token + " to email " + emailRequest.email());
        String subject = "Встановлення нового паролю";
        Content content = new Content("text/html", buildContent(token));
        sendMail(subject, emailRequest.email(), content);
        logger.info("sendToken() - Token was sent");
    }

    @Override
    public void sendNewPassword(String to, String newPassword) {
        logger.info("sendToken() - Sending password " + newPassword + " to email " + to);
        String subject = "Новий пароль";
        Content content = new Content("text/html", buildPasswordContent(newPassword));
        sendMail(subject, to, content);
        logger.info("sendToken() - Success send new password to email {}", to);
    }

    @Override
    public void sendMessage(String to, String subject, String messageHtml, Staff staff) {
        logger.info("sendMessage() - start send message to: {}", to);
        Content content = new Content("text/html", buildMessageContent(messageHtml, subject, staff));
        sendMail(subject, to, content);
        logger.info("sendMessage() -> message to: {}, has been send", to);
    }

    @Override
    public void sendInviteToStaff(String token, Staff staffById) {
        logger.info("sendInviteToStaff() -> start, with staffId: {}", staffById.getId());
        String subject = "Запрошення у систему";
        Content content = new Content("text/html", buildInviteContent(token, staffById));
        sendMail(subject, staffById.getEmail(), content);
        logger.info("sendInviteToStaff() -> end, invite has been send");
    }

    @Override
    public void sendActivationToOwner(String token, Long ownerId) {
        logger.info("sendActivationToOwner() - Sending activation with token " + token + " to owner with id " + ownerId);
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findById(ownerId).orElseThrow(()-> new EntityNotFoundException("Owner was not found by id "+ownerId));
        String subject = "Активація облікового запису";
        Content content = new Content("text/html", buildOwnerActivationContent(token));
        sendMail(subject, apartmentOwner.getEmail(), content);
        logger.info("sendActivationToOwner() - Token was sent");
    }

    @Override
    public void sendInvitationToOwner(InvitationRequest invitationRequest) {
        logger.info("sendInvitationToOwner() - Sending invitation to owner with email " + invitationRequest.email());
        String subject = "Запрошення власника";
        Content content = new Content("text/html", buildOwnerInvitationContent());
        sendMail(subject, invitationRequest.email(), content);
        logger.info("sendInvitationToOwner() - Invitation was sent");
    }

    private String buildOwnerInvitationContent() {
        String link = formOwnerInvitationLink();
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process("email/ownerInviteTemplate",context);
    }

    private String formOwnerInvitationLink() {
        String fullUrl = ServletUriComponentsBuilder.fromRequestUri(httpServletRequest).build().toUriString();
        int index = fullUrl.indexOf("admin");
        String baseUrl = fullUrl.substring(0, index);
        String link = baseUrl + "cabinet/register";
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

    private String buildContent(String token) {
        String link = formLink(token);
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process("email/passwordResetTokenEmailTemplate", context);
    }

    private String formLink(String token) {
        String fullUrl = ServletUriComponentsBuilder.fromRequestUri(httpServletRequest).build().toUriString();
        int index = fullUrl.indexOf("admin");
        String baseUrl = fullUrl.substring(0, index);
        String link = baseUrl + "admin/changePassword?token=" + token;
        return link;
    }

    private String buildPasswordContent(String newPassword) {
        Context context = new Context();
        context.setVariable("password", newPassword);
        return templateEngine.process("email/newPasswordTemplate", context);
    }

    private String buildMessageContent(String messageContent, String subject, Staff staff) {
        Context context = new Context();
        context.setVariable("messageContent", messageContent);
        context.setVariable("subject", subject);
        context.setVariable("staff", staff);
        context.setVariable("link", getLinkToUserMessages());
        return templateEngine.process("email/sendMessageTemplate", context);
    }

    private String getLinkToUserMessages() {
        StringBuffer requestURL = httpServletRequest.getRequestURL();
        String link = requestURL.substring(0, requestURL.lastIndexOf("admin"));
        link += "user/messages";
        return link;
    }

    private String buildInviteContent(String token, Staff staff) {
        String link = formLink(token);
        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("staffFullName", staff.getFirstName() + " " + staff.getLastName());
        return templateEngine.process("email/sendInviteTemplate", context);
    }
    private String buildOwnerActivationContent(String token) {
        String link = formOwnerActivationLink(token);
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process("email/ownerActivationTemplate", context);
    }
    private String formOwnerActivationLink(String token){
        String fullUrl = ServletUriComponentsBuilder.fromRequestUri(httpServletRequest).build().toUriString();
        int index = fullUrl.indexOf("admin");
        String baseUrl = fullUrl.substring(0, index);
        String link = baseUrl + "cabinet/changePassword?token=" + token;
        return link;
    }
}
