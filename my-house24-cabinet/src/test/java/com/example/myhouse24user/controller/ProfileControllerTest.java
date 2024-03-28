package com.example.myhouse24user.controller;

import com.example.myhouse24user.configuration.awsConfiguration.S3ResourceResolve;
import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.InvoiceStatus;
import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.model.owner.ApartmentOwnerRequest;
import com.example.myhouse24user.model.owner.EditOwnerResponse;
import com.example.myhouse24user.model.owner.ViewOwnerResponse;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import com.example.myhouse24user.securityFilter.RecaptchaFilter;
import com.example.myhouse24user.service.ApartmentOwnerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @MockBean
    private ApartmentOwnerService apartmentOwnerService;
    @MockBean
    private ApartmentOwnerRepo apartmentOwnerRepo;
    private static ViewOwnerResponse expectedViewOwnerResponse;
    private static EditOwnerResponse expectedEditOwnerResponse;
    @BeforeAll
    public static void setUp(){
        expectedViewOwnerResponse = new ViewOwnerResponse();
        expectedViewOwnerResponse.setId(1L);
        expectedViewOwnerResponse.setFirstName("first name");
        expectedViewOwnerResponse.setLastName("last name");
        expectedViewOwnerResponse.setMiddleName("middle name");
        expectedViewOwnerResponse.setEmail("email");

        expectedEditOwnerResponse = new EditOwnerResponse(1L,"001",
                "name", "name", "name", "12.09.1990",
                OwnerStatus.NEW, "about", "phone", "viber",
                "telegram", "email", "image");
    }

    @Test
    void getViewProfilePage() throws Exception {
        this.mockMvc.perform(get("/cabinet/profile").with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("profile/view-profile"));
    }

    @Test
    void getProfile() throws Exception {
        when(apartmentOwnerService.getViewOwnerResponse()).thenReturn(expectedViewOwnerResponse);
        this.mockMvc.perform(get("/cabinet/profile/get").with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(expectedViewOwnerResponse.getFirstName()))
                .andExpect(jsonPath("$.middleName").value(expectedViewOwnerResponse.getMiddleName()))
                .andExpect(jsonPath("$.email").value(expectedViewOwnerResponse.getEmail()))
                .andExpect(jsonPath("$.lastName").value(expectedViewOwnerResponse.getLastName()));
    }

    @Test
    void getEditProfilePage() throws Exception {
        this.mockMvc.perform(get("/cabinet/profile/edit").with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("profile/edit-profile"));
    }

    @Test
    void getProfileForEdit() throws Exception {
        when(apartmentOwnerService.getEditOwnerResponse()).thenReturn(expectedEditOwnerResponse);
        this.mockMvc.perform(get("/cabinet/profile/edit/get").with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(expectedEditOwnerResponse.firstName()))
                .andExpect(jsonPath("$.middleName").value(expectedEditOwnerResponse.middleName()))
                .andExpect(jsonPath("$.email").value(expectedEditOwnerResponse.email()))
                .andExpect(jsonPath("$.lastName").value(expectedEditOwnerResponse.lastName()));

    }

    @Test
    void updateProfile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());

        ApartmentOwnerRequest apartmentOwnerRequest = new ApartmentOwnerRequest(1L, "name",
                "name", "name", "12.09.1990", OwnerStatus.NEW,
                "about", "+380992401786","+380992401786",
                "@telegram", "email@gmail.com", "password",
                "password");

        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setId(1L);

        doNothing().when(apartmentOwnerService).updateProfile(any(ApartmentOwnerRequest.class), any(MultipartFile.class));
        when(apartmentOwnerRepo.findByTelegramUsernameAndDeletedIsFalse(anyString())).thenReturn(Optional.of(apartmentOwner));
        when(apartmentOwnerRepo.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.of(apartmentOwner));
        when(apartmentOwnerRepo.findByPhoneNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.of(apartmentOwner));
        when(apartmentOwnerRepo.findByViberNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.of(apartmentOwner));


        this.mockMvc.perform(multipart("/cabinet/profile/edit")
                        .file(multipartFile)
                        .flashAttr("apartmentOwnerRequest",apartmentOwnerRequest)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void updateProfile_Should_Fail_Validation() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());

        ApartmentOwnerRequest apartmentOwnerRequest = new ApartmentOwnerRequest(1L, "",
                "", "", "", OwnerStatus.NEW,
                "", "+380992401786","+380992401786",
                "@telegram", "email@gmail.com", "password",
                "password1");

        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setId(2L);

        doNothing().when(apartmentOwnerService).updateProfile(any(ApartmentOwnerRequest.class), any(MultipartFile.class));
        when(apartmentOwnerRepo.findByTelegramUsernameAndDeletedIsFalse(anyString())).thenReturn(Optional.of(apartmentOwner));
        when(apartmentOwnerRepo.findByEmailAndDeletedIsFalse(anyString())).thenReturn(Optional.of(apartmentOwner));
        when(apartmentOwnerRepo.findByPhoneNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.of(apartmentOwner));
        when(apartmentOwnerRepo.findByViberNumberAndDeletedIsFalse(anyString())).thenReturn(Optional.of(apartmentOwner));


        this.mockMvc.perform(multipart("/cabinet/profile/edit")
                        .file(multipartFile)
                        .flashAttr("apartmentOwnerRequest",apartmentOwnerRequest)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(7)));
    }



    @Test
    void getOwnerStatuses() throws Exception {
        this.mockMvc.perform(get("/cabinet/profile/get-statuses")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(3)))
                .andExpect(jsonPath("$.[0]").value(OwnerStatus.NEW.toString()))
                .andExpect(jsonPath("$.[1]").value(OwnerStatus.ACTIVE.toString()))
                .andExpect(jsonPath("$.[2]").value(OwnerStatus.DISABLED.toString()));
    }
}