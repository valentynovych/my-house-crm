package com.example.myhouse24user.controller;

import com.example.myhouse24user.service.MasterRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

    @BeforeEach
    void setUp() {
    }

    @Test
    void viewMasterRequests() throws Exception {
        // cabinet/master-requests
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
    void addMasterRequest() {
    }

    @Test
    void getMasterRequests() {
    }

    @Test
    void testAddMasterRequest() {
    }

    @Test
    void deleteMasterRequest() {
    }
}