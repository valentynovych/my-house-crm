package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.model.apartments.ApartmentExtendResponse;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.service.ApartmentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApartmentsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private ApartmentService apartmentService;
    @Autowired
    private ApartmentRepo apartmentRepo;

    @BeforeEach
    void setUp() {
        Mockito.clearInvocations(apartmentService);
    }

    @Test
    void viewApartmentsTable() throws Exception {
        // given
        var request = get("/my-house/admin/apartments")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("apartments/apartments")
                );
    }

    @Test
    void viewAddApartment() throws Exception {
        // given
        var request = get("/my-house/admin/apartments/add")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("apartments/add-apartment")
                );
    }

    @Test
    void viewEditApartment() throws Exception {
        // given
        var request = get("/my-house/admin/apartments/edit-apartment/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("apartments/edit-apartment")
                );
    }

    @Test
    void viewApartment() throws Exception {
        // given
        var request = get("/my-house/admin/apartments/view-apartment/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("apartments/view-apartment")
                );
    }

    @Test
    void addNewApartment() throws Exception {
        // given
        var addRequest = new ApartmentAddRequest();
        addRequest.setApartmentNumber("00001");
        addRequest.setHouseId(1L);
        addRequest.setSectionId(1L);
        addRequest.setFloorId(1L);
        addRequest.setOwnerId(1L);
        addRequest.setTariffId(1L);
        addRequest.setArea(100.0);
        addRequest.setPersonalAccountId(1L);


        var request = post("/admin/apartments/add")
                .with(user(userDetails))
                .flashAttr("apartmentAddRequest", addRequest);

        // when
        doReturn(Optional.empty())
                .when(apartmentRepo).findApartmentByPersonalAccount_Id(eq(1L));
        doNothing()
                .when(apartmentService).addNewApartment(any(ApartmentAddRequest.class));

        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(apartmentService, times(1))
                .addNewApartment(any(ApartmentAddRequest.class));
    }

    @Test
    void getApartments() throws Exception {
        // given
        var pageable = Pageable.ofSize(10);
        var apartmentResponse = new ApartmentResponse();
        apartmentResponse.setId(1L);
        apartmentResponse.setBalance(BigDecimal.valueOf(100.0));
        apartmentResponse.setApartmentNumber("00001");

        var request = get("/admin/apartments/get-apartments")
                .with(user(userDetails))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var cashSheetTableResponses = new PageImpl<>(
                List.of(apartmentResponse, apartmentResponse), pageable, 2L);

        // when
        doReturn(cashSheetTableResponses)
                .when(apartmentService).getApartments(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap());
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].apartmentNumber").value("00001"))
                .andExpect(jsonPath("$.content[0].balance").value(100.0));

        verify(apartmentService, times(1))
                .getApartments(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap());
    }

    @Test
    void getApartmentById() throws Exception {
        // given
        var apartmentResponse = new ApartmentExtendResponse();
        apartmentResponse.setId(1L);
        apartmentResponse.setApartmentNumber("00001");
        apartmentResponse.setArea(100.0);

        var request = get("/admin/apartments/get-apartment/1")
                .with(user(userDetails));

        // when
        doReturn(apartmentResponse)
                .when(apartmentService).getApartmentById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.apartmentNumber").value("00001"))
                .andExpect(jsonPath("$.area").value(100.0));

        verify(apartmentService, times(1))
                .getApartmentById(eq(1L));
    }

    @Test
    void getApartmentById_WhenApartmentByIdNotFound() throws Exception {
        // given
        var request = get("/admin/apartments/get-apartment/1")
                .with(user(userDetails));

        // when
        doThrow(new EntityNotFoundException("Apartment not found"))
                .when(apartmentService).getApartmentById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(apartmentService, times(1))
                .getApartmentById(eq(1L));
    }

    @Test
    void updateApartment() throws Exception {
        // given
        var addRequest = new ApartmentAddRequest();
        addRequest.setApartmentNumber("00001");
        addRequest.setHouseId(1L);
        addRequest.setSectionId(1L);
        addRequest.setFloorId(1L);
        addRequest.setOwnerId(1L);
        addRequest.setTariffId(1L);
        addRequest.setArea(100.0);
        addRequest.setPersonalAccountId(1L);


        var request = post("/admin/apartments/edit-apartment/1")
                .with(user(userDetails))
                .flashAttr("apartmentAddRequest", addRequest);

        // when
        doReturn(Optional.empty())
                .when(apartmentRepo).findApartmentByPersonalAccount_Id(eq(1L));
        doNothing()
                .when(apartmentService).updateApartment(eq(1L), any(ApartmentAddRequest.class));

        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(apartmentService, times(1))
                .updateApartment(eq(1L), any(ApartmentAddRequest.class));
    }

    @Test
    void deleteApartment() throws Exception {
        // given
        var request = delete("/admin/apartments/delete-apartment/1")
                .with(user(userDetails));

        // when
        doNothing().when(apartmentService).deleteApartment(eq(1L));

        this.mockMvc.perform(request)
                // then
                .andDo(print())
                .andExpect(status().isOk());
    }
}