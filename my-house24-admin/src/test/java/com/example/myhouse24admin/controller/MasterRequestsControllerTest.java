package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.MasterRequestStatus;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import com.example.myhouse24admin.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestEditRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestResponse;
import com.example.myhouse24admin.model.masterRequest.MasterRequestTableResponse;
import com.example.myhouse24admin.model.staff.StaffShortResponse;
import com.example.myhouse24admin.service.MasterRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MasterRequestsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private MasterRequestService masterRequestService;

    @BeforeEach
    void setUp() {
        clearInvocations(masterRequestService);
    }

    @Test
    void viewAddMasterRequest() throws Exception {
        // given
        var request = get("/my-house/admin/master-requests/add-request")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("master-requests/add-master-request")
                );
    }

    @Test
    void viewMasterRequests() throws Exception {
        // given
        var request = get("/my-house/admin/master-requests")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("master-requests/master-requests")
                );
    }

    @Test
    void viewEditMasterRequest() throws Exception {
        // given
        var request = get("/my-house/admin/master-requests/edit-request/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("master-requests/edit-master-request")
                );
    }

    @Test
    void viewMasterRequest() throws Exception {
        // given
        var request = get("/my-house/admin/master-requests/view-request/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("master-requests/view-master-request")
                );
    }

    @Test
    void addNewRequest() throws Exception {
        // given
        var addRequest = new MasterRequestAddRequest();
        addRequest.setVisitDate(Instant.now());
        addRequest.setApartmentOwnerId(1L);
        addRequest.setApartmentOwnerPhone("+380631234567");
        addRequest.setDescription("test");
        addRequest.setApartmentId(1L);
        addRequest.setMasterType("PLUMBER");
        addRequest.setStatus(MasterRequestStatus.NEW);
        addRequest.setMasterId(1L);
        addRequest.setComment("test");


        var request = post("/admin/master-requests/add-request")
                .with(user(userDetails))
                .flashAttr("request", addRequest);

        // when
        doNothing().when(masterRequestService).addNewMasterRequest(eq(addRequest));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk()
                );
        verify(masterRequestService, times(1)).addNewMasterRequest(eq(addRequest));
    }

    @Test
    void getMasterRequests() throws Exception {
        // given
        var pageable = Pageable.ofSize(10);
        var masterRequestTableResponse = new MasterRequestTableResponse(
                1L,
                Instant.now(),
                "description",
                new ApartmentResponse(),
                "+380631234567",
                new StaffShortResponse(),
                MasterRequestStatus.NEW,
                "PLUMBER");

        var request = get("/admin/master-requests/get-requests")
                .with(user(userDetails))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var masterRequestTableResponses = new PageImpl<>(
                List.of(masterRequestTableResponse, masterRequestTableResponse), pageable, 2L);

        // when
        doReturn(masterRequestTableResponses)
                .when(masterRequestService).getMasterRequests(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap());
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].visitDate").isNotEmpty())
                .andExpect(jsonPath("$.content[0].apartmentOwnerPhone").value("+380631234567"))
                .andExpect(jsonPath("$.content[0].description").value("description"))
                .andExpect(jsonPath("$.content[0].status").value("NEW"));

        verify(masterRequestService, times(1))
                .getMasterRequests(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap());
    }

    @Test
    void testGetMasterRequests() throws Exception {
        // given
        var masterRequestTableResponse = new MasterRequestResponse(
                1L,
                Instant.now(),
                "description",
                new ApartmentResponse(),
                "+380631234567",
                new StaffShortResponse(),
                MasterRequestStatus.NEW,
                Instant.now(),
                "PLUMBER");

        var request = get("/admin/master-requests/get-request/1")
                .with(user(userDetails));

        // when
        doReturn(masterRequestTableResponse)
                .when(masterRequestService).getMasterRequestById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.visitDate").isNotEmpty())
                .andExpect(jsonPath("$.apartmentOwnerPhone").value("+380631234567"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.status").value("NEW"));

        verify(masterRequestService, times(1))
                .getMasterRequestById(eq(1L));
    }

    @Test
    void updateRequest() throws Exception {
        // given
        var editRequest = new MasterRequestEditRequest();
        editRequest.setId(1L);
        editRequest.setVisitDate(Instant.now());
        editRequest.setApartmentOwnerId(1L);
        editRequest.setApartmentOwnerPhone("+380631234567");
        editRequest.setDescription("test");
        editRequest.setApartmentId(1L);
        editRequest.setMasterType("PLUMBER");
        editRequest.setStatus(MasterRequestStatus.NEW);
        editRequest.setMasterId(1L);
        editRequest.setComment("test");


        var request = post("/admin/master-requests/edit-request/1")
                .with(user(userDetails))
                .flashAttr("request", editRequest);

        // when
        doNothing().when(masterRequestService).updateMasterRequest(eq(1L), eq(editRequest));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk()
                );
        verify(masterRequestService, times(1)).updateMasterRequest(eq(1L), eq(editRequest));
    }

    @Test
    void deleteMasterRequest_WhenSuccessDelete() throws Exception {
        // given
        var request = delete("/admin/master-requests/delete/1")
                .with(user(userDetails));

        // when
        doReturn(true)
                .when(masterRequestService).deleteMasterRequestById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk()
                );
        verify(masterRequestService, times(1)).deleteMasterRequestById(eq(1L));
    }

    @Test
    void deleteMasterRequest_WhenDeleteIsFailed() throws Exception {
        // given
        var request = delete("/admin/master-requests/delete/1")
                .with(user(userDetails));

        // when
        doReturn(false)
                .when(masterRequestService).deleteMasterRequestById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isLocked()
                );
        verify(masterRequestService, times(1)).deleteMasterRequestById(eq(1L));
    }
}