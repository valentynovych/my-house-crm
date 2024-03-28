package com.example.myhouse24user.controller;

import com.example.myhouse24user.entity.MasterRequestStatus;
import com.example.myhouse24user.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24user.model.masterRequest.MasterRequestTableResponse;
import com.example.myhouse24user.service.MasterRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static com.example.myhouse24user.config.TestConfig.USER_EMAIL;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class MasterRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @MockBean
    private MasterRequestService masterRequestService;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = Pageable.ofSize(10);
    }

    @Test
    void viewMasterRequests() throws Exception {
        // given
        var request = get("/cabinet/master-requests")
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
    void addMasterRequest() throws Exception {
        // given
        var request = get("/cabinet/master-requests/add-request")
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
    void getMasterRequests() throws Exception {
        // given
        var request = get("/cabinet/master-requests/get-requests")
                .with(user(userDetails))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        Instant now = Instant.now();
        var masterRequestTableResponse = new MasterRequestTableResponse(
                1L,
                now,
                MasterRequestStatus.NEW,
                "description",
                "PLUMBER");
        var apartmentShortResponses = new PageImpl<>(
                List.of(masterRequestTableResponse), pageable, 1L);

        // when
        when(masterRequestService.getMasterRequests(eq(USER_EMAIL),
                eq(pageable.getPageNumber()), eq(pageable.getPageSize())))
                .thenReturn(apartmentShortResponses);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.content[0].id", is(1)),
                        jsonPath("$.content[0].visitDate", notNullValue()),
                        jsonPath("$.content[0].status", is("NEW")),
                        jsonPath("$.content[0].description", is("description")),
                        jsonPath("$.content[0].masterType", is("PLUMBER"))
                );
        verify(masterRequestService, times(1)).getMasterRequests(eq(USER_EMAIL), eq(pageable.getPageNumber()),
                eq(pageable.getPageSize()));
    }

    @Test
    void testAddMasterRequest() throws Exception {
        // given
        Instant now = Instant.now();
        var masterRequestAddRequest = new MasterRequestAddRequest();
        masterRequestAddRequest.setMasterType("PLUMBER");
        masterRequestAddRequest.setDescription("description");
        masterRequestAddRequest.setApartmentId(1L);
        masterRequestAddRequest.setVisitDate(now);

        var request = post("/cabinet/master-requests/add-request")
                .with(user(userDetails))
                .param("masterType", masterRequestAddRequest.getMasterType())
                .param("description", masterRequestAddRequest.getDescription())
                .param("apartmentId", String.valueOf(masterRequestAddRequest.getApartmentId()))
                .param("visitDate", String.valueOf(masterRequestAddRequest.getVisitDate()))
                .flashAttr("masterRequest", new MasterRequestAddRequest());


        // when
        doNothing()
                .when(masterRequestService).addMasterRequest(eq(masterRequestAddRequest), eq(USER_EMAIL));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());
        verify(masterRequestService, times(1))
                .addMasterRequest(any(MasterRequestAddRequest.class), eq(USER_EMAIL));
    }

    @Test
    void deleteMasterRequest() throws Exception {
        // given
        var request = delete("/cabinet/master-requests/delete/%s".formatted(1L))
                .with(user(userDetails));

        // when
        doNothing()
                .when(masterRequestService).deleteMasterRequest(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());
        verify(masterRequestService, times(1))
                .deleteMasterRequest(eq(1L));
    }
}