package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerShortResponse;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import com.example.myhouse24admin.model.houses.HouseShortResponse;
import com.example.myhouse24admin.model.houses.SectionResponse;
import com.example.myhouse24admin.model.personalAccounts.*;
import com.example.myhouse24admin.repository.PersonalAccountRepo;
import com.example.myhouse24admin.service.PersonalAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonalAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private PersonalAccountService personalAccountService;
    @Autowired
    private PersonalAccountRepo personalAccountRepo;

    @BeforeEach
    void setUp() {
        clearInvocations(personalAccountService);
    }

    @Test
    void viewPersonalAccounts() throws Exception {
        // given
        var request = get("/my-house/admin/personal-accounts")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("personalAccounts/personal-accounts")
                );
    }

    @Test
    void viewAddPersonalAccount() throws Exception {
        // given
        var request = get("/my-house/admin/personal-accounts/add")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("personalAccounts/add-personal-account")
                );
    }

    @Test
    void viewEditPersonalAccount() throws Exception {
        // given
        var request = get("/my-house/admin/personal-accounts/edit-account/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("personalAccounts/edit-personal-account")
                );
    }

    @Test
    void viewViewPersonalAccount() throws Exception {
        // given
        var request = get("/my-house/admin/personal-accounts/view-account/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("personalAccounts/view-personal-account")
                );
    }

    @Test
    void getAccountsFindByNumber() throws Exception {
        // given
        var personalAccountShortResponse = new PersonalAccountShortResponse();
        personalAccountShortResponse.setId(1L);
        personalAccountShortResponse.setAccountNumber("1L");

        List<PersonalAccountShortResponse> personalAccountShortResponses =
                List.of(personalAccountShortResponse, personalAccountShortResponse, personalAccountShortResponse);

        var request = get("/admin/personal-accounts/get-free-accounts-find-number")
                .with(user(userDetails))
                .param("page", "0")
                .param("pageSize", "10")
                .param("accountNumber", "1");

        // when
        doReturn(new PageImpl<>(personalAccountShortResponses, PageRequest.of(0, 10), personalAccountShortResponses.size()))
                .when(personalAccountService).getAccountsFindByNumber(eq(0), eq(10), anyString());
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.size()").value(3));

        verify(personalAccountService, times(1))
                .getAccountsFindByNumber(eq(0), eq(10), anyString());
    }

    @Test
    void getPersonalAccounts() throws Exception {
        // given
        var personalAccountTableResponse = new PersonalAccountTableResponse();
        personalAccountTableResponse.setId(1L);
        personalAccountTableResponse.setAccountNumber("00000-00001");
        personalAccountTableResponse.setStatus(PersonalAccountStatus.ACTIVE);
        personalAccountTableResponse.setApartment(new ApartmentResponse());

        List<PersonalAccountTableResponse> personalAccountTableResponses =
                List.of(personalAccountTableResponse, personalAccountTableResponse, personalAccountTableResponse);
        Map<String, String> searchParam = Map.of("accountNumber", "1");

        var request = get("/admin/personal-accounts/get-personal-accounts")
                .with(user(userDetails))
                .param("page", "0")
                .param("pageSize", "10")
                .requestAttr("searchParam", searchParam);

        // when
        doReturn(new PageImpl<>(personalAccountTableResponses, PageRequest.of(0, 10), personalAccountTableResponses.size()))
                .when(personalAccountService).getPersonalAccounts(eq(0), eq(10), anyMap());
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.size()").value(3));

        verify(personalAccountService, times(1))
                .getPersonalAccounts(eq(0), eq(10), anyMap());
    }

    @Test
    void getPersonalAccountStatuses() throws Exception {
        // given
        var personalAccountStatuses = Arrays.asList(PersonalAccountStatus.values());

        var request = get("/admin/personal-accounts/get-statuses")
                .with(user(userDetails));

        // when
        doReturn(personalAccountStatuses)
                .when(personalAccountService).getPersonalAccountStatuses();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(2));

        verify(personalAccountService, times(1))
                .getPersonalAccountStatuses();
    }

    @Test
    void addNewPersonalAccount_WhenRequestIsValid() throws Exception {
        // given
        var personalAccountAddRequest = new PersonalAccountAddRequest();
        personalAccountAddRequest.setAccountNumber("00000-00001");
        personalAccountAddRequest.setStatus(PersonalAccountStatus.ACTIVE);
        personalAccountAddRequest.setApartmentId(1L);

        var request = post("/admin/personal-accounts/add")
                .with(user(userDetails))
                .flashAttr("request", personalAccountAddRequest);

        // when
        doNothing()
                .when(personalAccountService).addNewPersonalAccount(any(PersonalAccountAddRequest.class));
        doReturn(false).
                when(personalAccountRepo).existsPersonalAccountByAccountNumber(eq("00000-00001"));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(personalAccountRepo, times(1))
                .existsPersonalAccountByAccountNumber(eq("00000-00001"));
        verify(personalAccountService, times(1))
                .addNewPersonalAccount(any(PersonalAccountAddRequest.class));
    }

    @Test
    void addNewPersonalAccount_WhenRequestNotValid() throws Exception {
        // given
        var personalAccountAddRequest = new PersonalAccountAddRequest();

        var request = post("/admin/personal-accounts/add")
                .with(user(userDetails))
                .flashAttr("request", personalAccountAddRequest);

        // when
        doNothing()
                .when(personalAccountService).addNewPersonalAccount(any(PersonalAccountAddRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(personalAccountService, never())
                .addNewPersonalAccount(any(PersonalAccountAddRequest.class));
    }

    @Test
    void getPersonalAccount() throws Exception {
        // given
        var personalAccountResponse = new PersonalAccountResponse();
        personalAccountResponse.setId(1L);
        personalAccountResponse.setAccountNumber("00000-00001");
        personalAccountResponse.setStatus(PersonalAccountStatus.ACTIVE);
        ApartmentResponse apartment = new ApartmentResponse();
        apartment.setId(1L);
        apartment.setApartmentNumber("00001");
        personalAccountResponse.setApartment(apartment);

        var request = get("/admin/personal-accounts/get-account/1")
                .with(user(userDetails));

        // when
        doReturn(personalAccountResponse)
                .when(personalAccountService).getPersonalAccountById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                            {
                                "id": 1,
                                "accountNumber": "00000-00001",
                                "status": "ACTIVE",
                                "apartment": {
                                    "id": 1,
                                    "apartmentNumber": "00001"
                                }
                            }
                        """));

        verify(personalAccountService, times(1))
                .getPersonalAccountById(eq(1L));
    }

    @Test
    void updatePersonalAccount_WhenRequestIsValid() throws Exception {
        // given
        var personalAccount = new PersonalAccount();
        personalAccount.setId(1L);
        var personalAccountUpdateRequest = new PersonalAccountUpdateRequest();
        personalAccountUpdateRequest.setId(1L);
        personalAccountUpdateRequest.setAccountNumber("00000-00001");
        personalAccountUpdateRequest.setStatus(PersonalAccountStatus.ACTIVE);
        personalAccountUpdateRequest.setApartmentId(1L);

        var request = post("/admin/personal-accounts/edit-account/1")
                .with(user(userDetails))
                .flashAttr("request", personalAccountUpdateRequest);

        // when
        doReturn(Optional.of(personalAccount))
                .when(personalAccountRepo).findPersonalAccountByAccountNumber(eq("00000-00001"));
        doNothing()
                .when(personalAccountService).updatePersonalAccount(any(PersonalAccountUpdateRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(personalAccountRepo, times(1)).findPersonalAccountByAccountNumber(eq("00000-00001"));
        verify(personalAccountService, times(1))
                .updatePersonalAccount(any(PersonalAccountUpdateRequest.class));
    }

    @Test
    void updatePersonalAccount_WhenRequestNotValid() throws Exception {
        // given
        var personalAccount = new PersonalAccount();
        personalAccount.setId(2L);
        var personalAccountUpdateRequest = new PersonalAccountUpdateRequest();
        personalAccountUpdateRequest.setId(1L);
        personalAccountUpdateRequest.setAccountNumber("2L");
        personalAccountUpdateRequest.setStatus(PersonalAccountStatus.ACTIVE);
        personalAccountUpdateRequest.setApartmentId(1L);

        var request = post("/admin/personal-accounts/edit-account/1")
                .with(user(userDetails))
                .flashAttr("request", personalAccountUpdateRequest);

        // when
        doReturn(Optional.of(personalAccount))
                .when(personalAccountRepo).findPersonalAccountByAccountNumber(eq("2L"));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(personalAccountRepo, times(1)).findPersonalAccountByAccountNumber(eq("2L"));
    }

    @Test
    void getMinimalFreeAccountNumber() throws Exception {
        // given
        var request = get("/admin/personal-accounts/get-minimal-free-account-number")
                .with(user(userDetails));

        // when
        doReturn("00000-00001")
                .when(personalAccountService).getMinimalFreeAccountNumber();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("00000-00001"));

        verify(personalAccountService, times(1)).getMinimalFreeAccountNumber();
    }

    @Test
    void exportToExcel() throws Exception {
        // given

        var searchParams = new HashMap<String, String>();
        searchParams.put("accountNumber", "1");

        var personalAccountTableResponse = new PersonalAccountTableResponse();
        personalAccountTableResponse.setId(1L);
        personalAccountTableResponse.setAccountNumber("1L");
        personalAccountTableResponse.setStatus(PersonalAccountStatus.ACTIVE);
        ApartmentResponse apartment = new ApartmentResponse();
        apartment.setId(1L);
        apartment.setApartmentNumber("00001");
        apartment.setBalance(BigDecimal.ONE);
        HouseShortResponse house = new HouseShortResponse();
        house.setId(1L);
        house.setName("test");
        apartment.setHouse(house);
        SectionResponse section = new SectionResponse();
        section.setName("test");
        apartment.setSection(section);
        apartment.setOwner(new ApartmentOwnerShortResponse(1L, "Test Full Name", "+380123456789"));
        personalAccountTableResponse.setApartment(apartment);
        var request = get("/admin/personal-accounts/export-to-excel")
                .with(user(userDetails))
                .requestAttr("searchParams", searchParams)
                .param("page", "0")
                .param("pageSize", "10");

        // when
        doReturn(List.of(personalAccountTableResponse, personalAccountTableResponse))
                .when(personalAccountService).exportToExcel(eq(0), eq(10), anyMap());
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(header().exists("Content-Disposition"));

        verify(personalAccountService, times(1)).exportToExcel(eq(0), eq(10), anyMap());
    }
}