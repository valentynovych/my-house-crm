package com.example.myhouse24rest.controller;

import com.example.myhouse24rest.model.apartment.ApartmentShortResponse;
import com.example.myhouse24rest.service.ApartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class ApartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ApartmentService apartmentService;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        pageable = Pageable.ofSize(10);
    }

    @Test
    public void testGetAllApartments_WhenAuthorized() throws Exception {
        // given
        var request = get("/api/v1/apartments/get-all-apartments")
                .with(jwt().jwt(builder -> builder.claim("role", "OWNER")))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var apartmentShortResponses = new PageImpl<>(
                List.of(new ApartmentShortResponse(1L, "00001", BigDecimal.ZERO)), pageable, 1L);

        // when
        when(apartmentService.getAllApartments(
                eq(pageable.getPageNumber()), eq(pageable.getPageSize()), any(Principal.class)))
                .thenReturn(apartmentShortResponses);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "content": [{"apartmentId": 1, "apartmentNumber": "00001", "balance": 0}]
                                }
                                """)
                );
        verify(apartmentService, times(1)).getAllApartments(eq(pageable.getPageNumber()),
                eq(pageable.getPageSize()), any(Principal.class));
    }

    @Test
    public void testGetAllApartments_Authorized_BadParams() throws Exception {
        // given
        var request = get("/api/v1/apartments/get-all-apartments")
                .with(jwt().jwt(builder -> builder.claim("role", "OWNER")))
                .param("page", "-1")
                .param("pageSize", "1");

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllApartments_WhenNotAuthorized() throws Exception {
        // given
        var request = get("/api/v1/apartments/get-all-apartments")
                .param("page", "0")
                .param("pageSize", "10");

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isUnauthorized()
                );
    }
}