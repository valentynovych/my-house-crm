package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.apartmentOwner.InvitationRequest;
import com.example.myhouse24admin.model.authentication.EmailRequest;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {
    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private SendGrid sendGrid;
    @Mock
    private ApartmentOwnerRepo apartmentOwnerRepo;
    @Mock
    private HttpServletRequest httpServletRequest;
    @InjectMocks
    private MailServiceImpl mailService;
    private static Staff staff;
    @BeforeAll
    public static void setUp(){
        staff = new Staff();
        staff.setLastName("last name");
        staff.setFirstName("first name");
    }
    @Test
    void sendToken_Should_Send_Token() throws IOException {
        mockForTemplateEngineAndSendGrid();

        mailService.sendToken("token", new EmailRequest("email"), "/admin/url");

        verifyForTemplateEngineAndSendGrid();
    }
    @Test
    void sendToken_Should_Throw_IOException() throws IOException {
        mockForTemplateEngineAndSendGridTrowException();

        mailService.sendToken("token", new EmailRequest("email"), "/admin/url");

        verifyForTemplateEngineAndSendGrid();
    }

    @Test
    void sendNewPassword_Should_Send_New_Password() throws IOException {
        mockForTemplateEngineAndSendGrid();

        mailService.sendNewPassword("to", "new password");

        verifyForTemplateEngineAndSendGrid();
    }
    @Test
    void sendNewPassword_Should_Throw_IOException() throws IOException {
        mockForTemplateEngineAndSendGridTrowException();

        mailService.sendNewPassword("to", "new password");

        verifyForTemplateEngineAndSendGrid();
    }

    @Test
    void sendMessage_Should_Send_New_Password() throws IOException {
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/admin/url"));
        mockForTemplateEngineAndSendGrid();

        mailService.sendMessage("to", "new message", "message", new Staff());

        verify(httpServletRequest, times(1)).getRequestURL();
        verifyNoMoreInteractions(httpServletRequest);
        verifyForTemplateEngineAndSendGrid();
    }
    @Test
    void sendMessage_Should_Throw_IOException() throws IOException {
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/admin/url"));
        mockForTemplateEngineAndSendGridTrowException();

        mailService.sendMessage("to", "new message", "message", staff);

        verify(httpServletRequest, times(1)).getRequestURL();
        verifyNoMoreInteractions(httpServletRequest);
        verifyForTemplateEngineAndSendGrid();
    }

    @Test
    void sendInviteToStaff_Should_Sent_Invitation_To_Staff() throws IOException {
        mockForTemplateEngineAndSendGrid();

        mailService.sendInviteToStaff("token", staff, "/admin/url");

        verifyForTemplateEngineAndSendGrid();
    }
    @Test
    void sendInviteToStaff_Should_Throw_IOException() throws IOException {
        mockForTemplateEngineAndSendGridTrowException();

        mailService.sendInviteToStaff("token", staff, "/admin/url");

        verifyForTemplateEngineAndSendGrid();
    }

    @Test
    void sendActivationToOwner_Should_Send_Activation_To_Owner() throws IOException {
        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setEmail("email");
        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.of(apartmentOwner));
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/admin/url"));
        mockForTemplateEngineAndSendGrid();

        mailService.sendActivationToOwner("token", 1L);

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verify(httpServletRequest, times(1)).getRequestURL();
        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(httpServletRequest);
        verifyForTemplateEngineAndSendGrid();
    }
    @Test
    void sendActivationToOwner_Should_Throw_IOException() throws IOException {
        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setEmail("email");
        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.of(apartmentOwner));
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/admin/url"));
        mockForTemplateEngineAndSendGridTrowException();

        mailService.sendActivationToOwner("token", 1L);

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verify(httpServletRequest, times(1)).getRequestURL();
        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(httpServletRequest);
        verifyForTemplateEngineAndSendGrid();
    }
    @Test
    void sendActivationToOwner_Should_Throw_EntityNotFoundException() {
        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setEmail("email");
        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                mailService.sendActivationToOwner("token", 1L));

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(apartmentOwnerRepo);
    }

    @Test
    void sendInvitationToOwner_Should_Send_Invitation_To_Owner() throws IOException {
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/admin/url"));
        mockForTemplateEngineAndSendGrid();

        mailService.sendInvitationToOwner(new InvitationRequest("email"));

        verify(httpServletRequest, times(1)).getRequestURL();
        verifyNoMoreInteractions(httpServletRequest);
        verifyForTemplateEngineAndSendGrid();
    }
    @Test
    void sendInvitationToOwner_Should_Throw_IOException() throws IOException {
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/admin/url"));
        mockForTemplateEngineAndSendGridTrowException();

        mailService.sendInvitationToOwner(new InvitationRequest("email"));

        verify(httpServletRequest, times(1)).getRequestURL();
        verifyNoMoreInteractions(httpServletRequest);
        verifyForTemplateEngineAndSendGrid();
    }

    @Test
    void sendInvoice_Should_Send_Invoice() throws IOException {
        when(sendGrid.api(any(Request.class))).thenReturn(new Response());

        mailService.sendInvoice("to", new byte[2]);

        verify(sendGrid, times(1)).api(any(Request.class));
        verifyNoMoreInteractions(sendGrid);
    }

    @Test
    void sendInvoice_Should_Throw_IOException() throws IOException {
        doThrow(IOException.class).when(sendGrid).api(any(Request.class));

        mailService.sendInvoice("to", new byte[2]);

        verify(sendGrid, times(1)).api(any(Request.class));
        verifyNoMoreInteractions(sendGrid);
    }

    private void mockForTemplateEngineAndSendGrid() throws IOException {
        when(templateEngine.process(anyString(), any())).thenReturn("template");
        when(sendGrid.api(any(Request.class))).thenReturn(new Response());
    }
    private void mockForTemplateEngineAndSendGridTrowException() throws IOException {
        when(templateEngine.process(anyString(), any())).thenReturn("template");
        doThrow(IOException.class).when(sendGrid).api(any(Request.class));
    }
    private void verifyForTemplateEngineAndSendGrid() throws IOException {
        verify(templateEngine, times(1)).process(anyString(), any());
        verify(sendGrid, times(1)).api(any(Request.class));

        verifyNoMoreInteractions(templateEngine);
        verifyNoMoreInteractions(sendGrid);
    }
}