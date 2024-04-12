package com.example.myhouse24rest.controller;

import com.example.myhouse24rest.model.apartment.ApartmentResponse;
import com.example.myhouse24rest.model.profile.ProfileResponse;
import com.example.myhouse24rest.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProfileService profileService;

    @Test
    void getProfile_WhenAuthorized() throws Exception {
        // given
        var request = get("/v1/profile/get-profile")
                .with(jwt().jwt(builder -> builder.claim("role", "OWNER")));

        var apartmentResponse = new ApartmentResponse(
                1L,
                "00001",
                "houseName",
                "address",
                "section",
                "floor",
                "00000-00001");
        var ownerProfile = new ProfileResponse(
                "Jon Snow",
                "00001",
                "owner.email@example.com",
                "+38050000000",
                List.of(apartmentResponse));

        // when
        when(profileService.getProfile(any(Principal.class)))
                .thenReturn(ownerProfile);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "fullName": "Jon Snow",
                                    "profileId": "00001",
                                    "email": "owner.email@example.com",
                                    "phoneNumber": "+38050000000",
                                    "myApartments": [
                                        {
                                            "apartmentId": 1,
                                            "apartmentNumber": "00001",
                                            "houseName": "houseName",
                                            "address": "address",
                                            "section": "section",
                                            "floor": "floor",
                                            "personalAccountNumber": "00000-00001"
                                        }]
                                }
                                """)
                );
        verify(profileService, times(1)).getProfile(any(Principal.class));
    }

    @Test
    void getProfile_WhenNotAuthorized() throws Exception {
        // given
        var request = get("/v1/profile/get-profile");

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}