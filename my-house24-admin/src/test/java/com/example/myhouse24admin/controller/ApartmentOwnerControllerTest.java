package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.InvoiceStatus;
import com.example.myhouse24admin.entity.OwnerStatus;
import com.example.myhouse24admin.model.apartmentOwner.*;
import com.example.myhouse24admin.model.meterReadings.HouseNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.service.ApartmentOwnerService;
import com.example.myhouse24admin.service.HouseService;
import com.example.myhouse24admin.service.MailService;
import com.example.myhouse24admin.service.OwnerPasswordResetTokenService;
import com.thoughtworks.qdox.directorywalker.Filter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class ApartmentOwnerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails userDetails;

    @Autowired
    private ApartmentOwnerService apartmentOwnerService;

    @Autowired
    private OwnerPasswordResetTokenService ownerPasswordResetTokenService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private MailService mailService;
    private static Pageable pageable;
    @BeforeAll
    static void setUp() {
        pageable = PageRequest.of(0,1);
    }

    @Test
    void getOwnersPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/owners")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("owners/owners"));
    }

    @Test
    void getOwners() throws Exception {
        TableApartmentOwnerResponse tableApartmentOwnerResponse = new TableApartmentOwnerResponse();
        tableApartmentOwnerResponse.setEmail("email");
        tableApartmentOwnerResponse.setHouse("house");

        when(apartmentOwnerService.getApartmentOwnerResponsesForTable(anyInt(), anyInt(), any(FilterRequest.class)))
                .thenReturn(new PageImpl<>(List.of(tableApartmentOwnerResponse), pageable,5));

        this.mockMvc.perform(get("/my-house/admin/owners/getOwners")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("page", "0")
                        .param("pageSize","1")
                        .param("ownerId","1")
                        .param("fullName","name")
                        .param("phoneNumber","phone")
                        .param("email","email")
                        .param("houseId","1")
                        .param("apartment","23")
                        .param("status","NEW")
                        .param("hasDebt","true")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].email").value(tableApartmentOwnerResponse.getEmail()))
                .andExpect(jsonPath("$.content[0].house").value(tableApartmentOwnerResponse.getHouse()));
    }

    @Test
    void getOwnerPageForCreate() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/owners/add")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("owners/owner"))
                .andExpect(model().attributeExists("statusLink"));
    }

    @Test
    void getOwnerStatuses() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/owners/get-statuses")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(OwnerStatus.NEW.toString()))
                .andExpect(jsonPath("$.[1]").value(OwnerStatus.ACTIVE.toString()))
                .andExpect(jsonPath("$.[2]").value(OwnerStatus.DISABLED.toString()));
    }

    @Test
    void createOwner_CreateApartmentOwnerRequest_Valid() throws Exception {
        CreateApartmentOwnerRequest createApartmentOwnerRequest =
                new CreateApartmentOwnerRequest("name", "name",
                        "name", "12.03.1990", OwnerStatus.NEW,
                        "about", "+380992403645", "viber",
                        "telegram", "email@gmail.com",
                        "Anastasiia12/","Anastasiia12/");

        doNothing().when(apartmentOwnerService).createApartmentOwner(any(CreateApartmentOwnerRequest.class), any(MultipartFile.class));

        this.mockMvc.perform(post("/my-house/admin/owners/add")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("createApartmentOwnerRequest", createApartmentOwnerRequest))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void createOwner_CreateApartmentOwnerRequest_Not_Valid() throws Exception {
        CreateApartmentOwnerRequest createApartmentOwnerRequest =
                new CreateApartmentOwnerRequest("", "", "",
                        null, OwnerStatus.NEW, "",
                        "38", "bbbbbbbbbbbbbbb", "",
                        "", "Anastasiia","");

        this.mockMvc.perform(post("/my-house/admin/owners/add")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("createApartmentOwnerRequest", createApartmentOwnerRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(10)));
    }
    @Test
    void getOwnerPageForEdit() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/owners/edit/{id}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("owners/owner"))
                .andExpect(model().attributeExists("statusLink"));
    }

    @Test
    void getOwnerForEdit() throws Exception {
        ApartmentOwnerResponse apartmentOwnerResponse = new ApartmentOwnerResponse("001",
                "name", "name", "name","12.03.1990",
                OwnerStatus.NEW, "about","phone", "viber",
                "telegram", "email", "image");

        when(apartmentOwnerService.getApartmentOwnerResponse(anyLong()))
                .thenReturn(apartmentOwnerResponse);

        this.mockMvc.perform(get("/my-house/admin/owners/edit/get-owner/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(apartmentOwnerResponse.email()))
                .andExpect(jsonPath("$.aboutOwner").value(apartmentOwnerResponse.aboutOwner()))
                .andExpect(jsonPath("$.birthDate").value(apartmentOwnerResponse.birthDate()));
    }

    @Test
    void updateOwner_EditApartmentOwnerRequest_Valid() throws Exception {
        EditApartmentOwnerRequest editApartmentOwnerRequest =
                new EditApartmentOwnerRequest("name", "name", "name",
                        "12.03.1990", OwnerStatus.NEW, "about",
                        "+380992403645", "viber", "telegram",
                        "email@gmail.com", "Anastasiia12/","Anastasiia12/");

        doNothing().when(apartmentOwnerService)
                .updateApartmentOwner(any(EditApartmentOwnerRequest.class), anyLong(),
                        any(MultipartFile.class));


        this.mockMvc.perform(post("/my-house/admin/owners/edit/{id}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("editApartmentOwnerRequest", editApartmentOwnerRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost/my-house/admin/owners"));
    }

    @Test
    void updateOwner_EditApartmentOwnerRequest_Not_Valid() throws Exception {
        EditApartmentOwnerRequest editApartmentOwnerRequest =
                new EditApartmentOwnerRequest("", "", "",
                        null, OwnerStatus.NEW, "",
                        "38", "bbbbbbbbbbbbbbb", "",
                        "", "Anastasiia","");

        this.mockMvc.perform(post("/my-house/admin/owners/edit/{id}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("editApartmentOwnerRequest", editApartmentOwnerRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(9)));
    }
    @Test
    void deleteOwner_Status_OK() throws Exception {
        when(apartmentOwnerService.deleteOwnerById(anyLong())).thenReturn(true);

        this.mockMvc.perform(get("/my-house/admin/owners/delete/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void deleteOwner_Status_CONFLICT() throws Exception {
        when(apartmentOwnerService.deleteOwnerById(anyLong())).thenReturn(false);

        this.mockMvc.perform(get("/my-house/admin/owners/delete/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void getViewOwnerPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/owners/view-owner/{id}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("owners/view-owner"));
    }

    @Test
    void getViewOwner() throws Exception {
        ViewApartmentOwnerResponse viewApartmentOwnerResponse = new ViewApartmentOwnerResponse("001",
                "name", "name", "name","12.03.1990",
                OwnerStatus.NEW, "about","phone", "viber",
                "telegram", "email", "image");

        when(apartmentOwnerService.getApartmentOwnerResponseForView(anyLong())).thenReturn(viewApartmentOwnerResponse);

        this.mockMvc.perform(get("/my-house/admin/owners/view-owner/get/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(viewApartmentOwnerResponse.email()))
                .andExpect(jsonPath("$.aboutOwner").value(viewApartmentOwnerResponse.aboutOwner()))
                .andExpect(jsonPath("$.birthDate").value(viewApartmentOwnerResponse.birthDate()));
    }

    @Test
    void getOwnersShortDetails() throws Exception {
        ApartmentOwnerShortResponse apartmentOwnerShortResponse = new ApartmentOwnerShortResponse(1L, "name", "phone");

        when(apartmentOwnerService.getShortResponseOwners(anyInt(), anyInt(), anyString()))
                .thenReturn(new PageImpl<>(List.of(apartmentOwnerShortResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/owners/get-owners")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("page", "0")
                        .param("pageSize","1")
                        .param("fullName","name"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].fullName").value(apartmentOwnerShortResponse.fullName()))
                .andExpect(jsonPath("$.content[0].phoneNumber").value(apartmentOwnerShortResponse.phoneNumber()));
    }

    @Test
    void getHouses() throws Exception {
        HouseNameResponse houseNameResponse = new HouseNameResponse(1L, "house");
        when(houseService.getHousesForSelect(any(SelectSearchRequest.class)))
                .thenReturn(new PageImpl<>(List.of(houseNameResponse), pageable, 5));

        this.mockMvc.perform(get("/my-house/admin/owners/get-houses")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("search", "search")
                        .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(houseNameResponse.id()))
                .andExpect(jsonPath("$.content[0].name").value(houseNameResponse.name()));
    }

    @Test
    void sendActivation() throws Exception {
        when(ownerPasswordResetTokenService.createOrUpdatePasswordResetToken(anyLong()))
                .thenReturn("token");
        doNothing().when(mailService).sendActivationToOwner(anyString(), anyLong());

        this.mockMvc.perform(post("/my-house/admin/owners/send-activation/{id}", 1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getSendInvitationPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/owners/send-invitation")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("owners/send-invitation"));
    }

    @Test
    void sendInvitation_InvitationRequest_Valid() throws Exception {
        InvitationRequest invitationRequest = new InvitationRequest("email@gmail.com");
        doNothing().when(mailService).sendInvitationToOwner(any(InvitationRequest.class));

        this.mockMvc.perform(post("/my-house/admin/owners/send-invitation")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("invitationRequest", invitationRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost/my-house/admin/owners"));

    }
    @Test
    void sendInvitation_InvitationRequest_Not_Valid() throws Exception {
        InvitationRequest invitationRequest = new InvitationRequest("");

        this.mockMvc.perform(post("/my-house/admin/owners/send-invitation")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("invitationRequest", invitationRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(1)));
    }
}