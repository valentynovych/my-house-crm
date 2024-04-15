package com.example.controller;

import com.example.configuration.awsConfiguration.S3ResourceResolve;
import com.example.entity.ServicePageBlock;
import com.example.service.ServicePageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ServicePageController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ServicePageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ServicePageService servicePageService;
    @MockBean
    private S3ResourceResolve s3ResourceResolve;
    private static ServicePageBlock servicePageBlock;
    @BeforeAll
    public static void setUp(){
        servicePageBlock = new ServicePageBlock();
        servicePageBlock.setId(1L);
        servicePageBlock.setTitle("title");
        servicePageBlock.setDescription("description");
        servicePageBlock.setImage("image");
    }
    @Test
    void getServicePage() throws Exception {
        this.mockMvc.perform(get("/services")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("service/service"));
    }

    @Test
    void getServicePageBlocks() throws Exception {
        Pageable pageable = PageRequest.of(0,1);
        when(servicePageService.getServicePageBlocks(anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(servicePageBlock), pageable, 5));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page","0");
        params.add("pageSize", "1");
        this.mockMvc.perform(get("/services/get").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value(servicePageBlock.getTitle()))
                .andExpect(jsonPath("$.content[0].description").value(servicePageBlock.getDescription()));
    }
}