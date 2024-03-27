package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.apartments.ApartmentShortResponse;
import com.example.myhouse24user.service.ApartmentService;
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

import java.util.List;

import static com.example.myhouse24user.config.TestConfig.USER_EMAIL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class ApartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ApartmentService apartmentService;
    @Autowired
    private UserDetails userDetails;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = Pageable.ofSize(10);
    }

    @Test
    void getOwnerApartments() throws Exception {
        // given
        var request = get("/cabinet/apartments/get-owner-apartments")
                .with(user(userDetails))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var apartmentShortResponses = new PageImpl<>(
                List.of(new ApartmentShortResponse(1L, "00001")), pageable, 1L);

        // when
        when(apartmentService.getOwnerApartments(eq(USER_EMAIL),
                eq(pageable.getPageNumber()), eq(pageable.getPageSize())))
                .thenReturn(apartmentShortResponses);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "content": [{"id": 1, "apartmentNumber": "00001"}], "numberOfElements": 1
                                }
                                """)
                );
        verify(apartmentService, times(1)).getOwnerApartments(eq(USER_EMAIL), eq(pageable.getPageNumber()),
                eq(pageable.getPageSize()));
    }
}