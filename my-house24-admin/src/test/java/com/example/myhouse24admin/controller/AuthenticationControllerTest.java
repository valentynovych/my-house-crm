package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.authentication.EmailRequest;
import com.example.myhouse24admin.model.authentication.ForgotPasswordRequest;
import com.example.myhouse24admin.service.MailService;
import com.example.myhouse24admin.service.PasswordResetTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
    private PasswordResetTokenService passwordResetTokenService;
    @Autowired
    private MailService mailService;
    @BeforeEach
    void setUp() {
    }

    @Test
    void getLoginPage() throws Exception {
        this.mockMvc.perform(get("/admin/login"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/login"));
    }

    @Test
    void getForgotPasswordPage() throws Exception {
        this.mockMvc.perform(get("/admin/forgotPassword"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/forgotPassword"));
    }

    @Test
    void sendPasswordResetToken_EmailRequest_Valid() throws Exception {
        when(passwordResetTokenService.createOrUpdatePasswordResetToken(any(EmailRequest.class)))
                .thenReturn("token");
        doNothing().when(mailService).sendToken(anyString(), any(EmailRequest.class), anyString());

        this.mockMvc.perform(post("/admin/forgotPassword")
                        .flashAttr("emailRequest",new EmailRequest("ruduknasta13@gmail.com")))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void sendPasswordResetToken_EmailRequest_Not_Valid() throws Exception {
        this.mockMvc.perform(post("/admin/forgotPassword")
                        .flashAttr("emailRequest",new EmailRequest("")))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(1)));
    }
    @Test
    void getSentTokenPage() throws Exception {
        this.mockMvc.perform(get("/admin/sentToken"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/sentToken"));
    }

    @Test
    void changePassword_Token_Valid() throws Exception {
        when(passwordResetTokenService.isPasswordResetTokenValid(anyString())).thenReturn(true);

        this.mockMvc.perform(get("/admin/changePassword")
                        .param("token","token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("token"))
                .andExpect(view().name("security/changePassword"));
    }
    @Test
    void changePassword_Token_Not_Valid() throws Exception {
        when(passwordResetTokenService.isPasswordResetTokenValid(anyString())).thenReturn(false);

        this.mockMvc.perform(get("/admin/changePassword")
                        .param("token","token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/tokenExpired"));
    }

    @Test
    void setNewPassword_Token_Valid() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest =
                new ForgotPasswordRequest("Anastasiia12/", "Anastasiia12/");
        when(passwordResetTokenService.isPasswordResetTokenValid(anyString())).thenReturn(true);
        doNothing().when(passwordResetTokenService).updatePassword(anyString(), anyString());

        this.mockMvc.perform(post("/admin/changePassword")
                        .param("token","token")
                        .flashAttr("forgotPasswordRequest", forgotPasswordRequest))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void setNewPassword_Token_Not_Valid() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest =
                new ForgotPasswordRequest("Anastasiia12/", "Anastasiia12/");
        when(passwordResetTokenService.isPasswordResetTokenValid(anyString())).thenReturn(false);

        this.mockMvc.perform(post("/admin/changePassword")
                        .param("token","token")
                        .flashAttr("forgotPasswordRequest", forgotPasswordRequest))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    @Test
    void setNewPassword_ForgotPasswordRequest_Not_Valid() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest =
                new ForgotPasswordRequest("", "Anastasiia");
        this.mockMvc.perform(post("/admin/changePassword")
                        .param("token","token")
                        .flashAttr("forgotPasswordRequest", forgotPasswordRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(2)));
    }
    @Test
    void getSuccessPage() throws Exception {
        this.mockMvc.perform(get("/admin/success"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/success"));
    }

    @Test
    void getTokenExpiredPage() throws Exception {
        this.mockMvc.perform(get("/admin/tokenExpired"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("security/tokenExpired"));
    }
}