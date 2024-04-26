package com.example.myhouse24admin.controller.siteManagement;

import com.example.myhouse24admin.entity.ServicePageBlock;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicePageBlockRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicePageRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicesPageResponse;
import com.example.myhouse24admin.service.ServicesPageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class ServicesPageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails userDetails;

    @Autowired
    private ServicesPageService servicesPageService;

    @Test
    void getServicesPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/site-management/service-page")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("site-management/services-page"));
    }

    @Test
    void getServicesPageResponse() throws Exception {
        ServicePageBlock servicePageBlock = new ServicePageBlock();
        servicePageBlock.setTitle("title");
        servicePageBlock.setDescription("description");
        ServicesPageResponse servicesPageResponse = new ServicesPageResponse(List.of(servicePageBlock),
                        "seoTitle", "seoDescription",
                        "seoKeyword");
        when(servicesPageService.getServicesPageResponse()).thenReturn(servicesPageResponse);

        this.mockMvc.perform(get("/my-house/admin/site-management/service-page/get")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicePageBlocks[0].title").value(servicesPageResponse.servicePageBlocks().get(0).getTitle()))
                .andExpect(jsonPath("$.servicePageBlocks[0].description").value(servicesPageResponse.servicePageBlocks().get(0).getDescription()))
                .andExpect(jsonPath("$.seoTitle").value(servicesPageResponse.seoTitle()));
    }

    @Test
    void updateServicesPage_ServicePageRequest_Valid() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        ServicePageRequest servicePageRequest = new ServicePageRequest();
        ServicePageBlockRequest servicePageBlockRequest = new ServicePageBlockRequest();
        servicePageBlockRequest.setTitle("title");
        servicePageBlockRequest.setDescription("description");
        servicePageBlockRequest.setDescriptionWithoutTags("description");
        servicePageBlockRequest.setImage(multipartFile);
        servicePageBlockRequest.setId(1L);
        servicePageRequest.setServicePageBlocks(List.of(servicePageBlockRequest));

        doNothing().when(servicesPageService).updateServicesPage(any(ServicePageRequest.class));

        this.mockMvc.perform(post("/my-house/admin/site-management/service-page")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("servicePageRequest", servicePageRequest))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void updateServicesPage_ServicePageRequest_Not_Valid() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        ServicePageRequest servicePageRequest = new ServicePageRequest();
        ServicePageBlockRequest servicePageBlockRequest = new ServicePageBlockRequest();
        servicePageBlockRequest.setTitle("");
        servicePageBlockRequest.setDescription("");
        servicePageBlockRequest.setImage(multipartFile);
        servicePageBlockRequest.setId(1L);
        servicePageRequest.setServicePageBlocks(List.of(servicePageBlockRequest));

        this.mockMvc.perform(post("/my-house/admin/site-management/service-page")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("servicePageRequest", servicePageRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(3)));
    }
}