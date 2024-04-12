package com.example.myhouse24rest.controller;

import com.example.myhouse24rest.model.auth.AuthRequest;
import com.example.myhouse24rest.model.auth.JwtResponse;
import com.example.myhouse24rest.model.auth.RefreshTokenRequest;
import com.example.myhouse24rest.service.AuthenticationService;
import com.example.myhouse24rest.utils.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private UserDetails userDetails;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    public void setUp() {
        accessToken = jwtTokenUtils.createAccessToken(userDetails);
        refreshToken = jwtTokenUtils.createRefreshToken(userDetails);
    }


    @Test
    void login_WhenBodyIsCorrect() throws Exception {

        // given
        var request = post("/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "5wqQ9@example.com",
                            "password": "Password123!@"
                        }
                        """);

        var jwtResponse = new JwtResponse(accessToken, refreshToken);

        // when
        when(authenticationService.login(any(AuthRequest.class)))
                .thenReturn(jwtResponse);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "accessToken": "%s",
                                    "refreshToken": "%s"
                                }
                                """.formatted(accessToken, refreshToken))
                );
        verify(authenticationService, times(1)).login(any(AuthRequest.class));

    }

    @Test
    void login_WhenBodyIsInCorrect() throws Exception {

        // given
        var request = post("/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test",
                            "password": "test"
                        }
                        """);

        // when
        this.mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WhenBodyIsInCorrect_BadRequest() throws Exception {

        // given
        var request = post("/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "5wqQ9@example.com",
                            "password": "Password123!@"
                        }
                        """);

        // when
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationService).login(any(AuthRequest.class));

        this.mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_WhenBodyIsCorrect() throws Exception {
        // given
        var request = post("/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "5wqQ9@example.com",
                            "refreshToken": "refreshToken"
                        }
                        """);

        var jwtResponse = new JwtResponse(accessToken, refreshToken);

        // when
        doReturn(jwtResponse)
                .when(authenticationService).refreshToken(any(RefreshTokenRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "accessToken": "%s",
                                    "refreshToken": "%s"
                                }
                                """.formatted(accessToken, refreshToken))
                );
        verify(authenticationService, times(1)).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    void refreshToken_WhenBodyIsInCorrect() throws Exception {
        // given
        var request = post("/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test",
                            "refreshToken": "%s"
                        }
                        """.formatted(refreshToken));


        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_WhenBodyIsCorrect_RefreshTokenIsNotValid() throws Exception {
        // given
        var request = post("/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "5wqQ9@example.com",
                            "refreshToken": "%s"
                        }
                        """.formatted(refreshToken));

        // when
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationService).refreshToken(any(RefreshTokenRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest());
    }
}