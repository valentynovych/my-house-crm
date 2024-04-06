package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.houses.FloorResponse;
import com.example.myhouse24admin.service.FloorService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FloorsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private FloorService floorService;

    @Test
    void getFloorsByHouseId() throws Exception {
        // given
        var pageable = Pageable.ofSize(10);
        var floorResponse = new FloorResponse();
        floorResponse.setId(1L);
        floorResponse.setName("test");

        var request = get("/admin/floors/get-floors-by-house/1")
                .with(user(userDetails))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()))
                .param("name", "test");

        var houseShortResponses = new PageImpl<>(
                List.of(floorResponse, floorResponse), pageable, 2L);

        // when
        doReturn(houseShortResponses)
                .when(floorService).getFloorsByHouseId(eq(1L), eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyString());
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("test"));

        verify(floorService, times(1))
                .getFloorsByHouseId(eq(1L), eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyString());
    }
}