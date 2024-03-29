package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.authentication.EmailRequest;
import com.example.myhouse24user.model.authentication.ForgotPasswordRequest;
import com.example.myhouse24user.model.authentication.RegistrationRequest;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import com.example.myhouse24user.service.ApartmentOwnerService;
import com.example.myhouse24user.service.MailService;
import com.example.myhouse24user.service.OwnerPasswordResetTokenService;
import com.example.myhouse24user.service.RecaptchaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RecaptchaService recaptchaService;
    @MockBean
    private OwnerPasswordResetTokenService ownerPasswordResetTokenService;
    @MockBean
    private MailService mailService;
    @MockBean
    private ApartmentOwnerService apartmentOwnerService;
    @MockBean
    private ApartmentOwnerRepo apartmentOwnerRepo;


    @Test
    void getLoginPage() throws Exception {
        this.mockMvc.perform(get("/cabinet/login"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/login"));
    }

    @Test
    void getForgotPasswordPage() throws Exception {
        this.mockMvc.perform(get("/cabinet/forgotPassword"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/forgotPassword"));
    }

    @Test
    void sendPasswordResetToken() throws Exception {
        when(ownerPasswordResetTokenService.createOrUpdatePasswordResetToken(any(EmailRequest.class)))
                .thenReturn("token");
        doNothing().when(mailService).sendToken(anyString(), any(EmailRequest.class), anyString());
        when(apartmentOwnerRepo.existsApartmentOwnerByEmail(anyString())).thenReturn(true);
        this.mockMvc.perform(post("/cabinet/forgotPassword")
                        .flashAttr("emailRequest",new EmailRequest("email")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getSentTokenPage() throws Exception {
        this.mockMvc.perform(get("/cabinet/sentToken"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/sentToken"));
    }

    @Test
    void changePassword_Should_Return_ChangePassword_View() throws Exception {
        when(ownerPasswordResetTokenService.isPasswordResetTokenValid(anyString())).thenReturn(true);
        this.mockMvc.perform(get("/cabinet/changePassword")
                        .param("token","token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/changePassword"));
    }
    @Test
    void changePassword_Should_Return_TokenExpired_View() throws Exception {
        when(ownerPasswordResetTokenService.isPasswordResetTokenValid(anyString())).thenReturn(false);
        this.mockMvc.perform(get("/cabinet/changePassword")
                        .param("token","token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/tokenExpired"));
    }

    @Test
    void setNewPassword_Token_Should_Be_Valid() throws Exception {
        when(ownerPasswordResetTokenService.isPasswordResetTokenValid(anyString())).thenReturn(true);
        doNothing().when(ownerPasswordResetTokenService).updatePassword(anyString(), anyString());
        this.mockMvc.perform(post("/cabinet/changePassword")
                        .param("token","token")
                        .flashAttr("forgotPasswordRequest",
                                new ForgotPasswordRequest("Anastasiia12/","Anastasiia12/")))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void setNewPassword_Token_Should_Not_Be_Valid() throws Exception {
        when(ownerPasswordResetTokenService.isPasswordResetTokenValid(anyString())).thenReturn(false);
        this.mockMvc.perform(post("/cabinet/changePassword")
                        .param("token","token")
                        .flashAttr("forgotPasswordRequest",
                                new ForgotPasswordRequest("Anastasiia12/","Anastasiia12/")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getSuccessPage() throws Exception {
        this.mockMvc.perform(get("/cabinet/success"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/success"));
    }

    @Test
    void getTokenExpiredPage() throws Exception {
        this.mockMvc.perform(get("/cabinet/tokenExpired"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/tokenExpired"));
    }

    @Test
    void getRegisterPage() throws Exception {
        this.mockMvc.perform(get("/cabinet/register"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("registration/registration"));
    }

    @Test
    void registerOwner_Recaptcha_Should_Be_valid() throws Exception {
        when(recaptchaService.isRecaptchaValid(anyString())).thenReturn(true);
        doNothing().when(apartmentOwnerService).register(any(RegistrationRequest.class));
        this.mockMvc.perform(post("/cabinet/register")
                        .param("recaptcha","recaptcha")
                        .flashAttr("registrationRequest",
                                new RegistrationRequest("name","name",
                                        "name", "email@gmail.com",
                                        "Paasword1/", "Paasword1/",
                                        true)))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void registerOwner_Recaptcha_Should_Not_Be_valid() throws Exception {
        when(recaptchaService.isRecaptchaValid(anyString())).thenReturn(false);
        this.mockMvc.perform(post("/cabinet/register")
                        .param("recaptcha","recaptcha")
                        .flashAttr("registrationRequest",
                                new RegistrationRequest("name","name",
                                        "name", "email@gmail.com",
                                        "Paasword1/", "Paasword1/",
                                        true)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.grecaptha").exists());
    }
}