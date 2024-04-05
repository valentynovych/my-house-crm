package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.houses.SectionResponse;
import com.example.myhouse24admin.service.SectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SectionsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private SectionService sectionService;

    @Test
    void getSectionsByHouseId() throws Exception {
        // given
        var sectionResponse = new SectionResponse();
        sectionResponse.setId(1L);
        sectionResponse.setName("Section 1");
        sectionResponse.setRangeApartmentNumbers("001-100");
        var request = get("/admin/sections/get-sections-by-house/1")
                .with(user(userDetails))
                .param("page", "0")
                .param("pageSize", "10")
                .param("name", "Section 1");
        var sectionResponsePage = new PageImpl<>(List.of(sectionResponse, sectionResponse), PageRequest.of(0, 10), 2);

        // when
        doReturn(sectionResponsePage)
                .when(sectionService).getSectionsByHouseId(eq(1L), eq(0), eq(10), eq("Section 1"));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.size()").value(sectionResponsePage.getTotalElements()))
                .andExpect(jsonPath("$.content[0].name").value(sectionResponse.getName()));

        verify(sectionService, times(1)).getSectionsByHouseId(eq(1L), eq(0), eq(10), eq("Section 1"));
    }
}