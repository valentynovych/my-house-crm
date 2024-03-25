package com.example.controller;

import com.example.configuration.awsConfiguration.S3ResourceResolve;
import com.example.model.aboutPage.AboutPageResponse;
import com.example.service.AboutPageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = AboutPageController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AboutPageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AboutPageService aboutPageService;
    @MockBean
    private S3ResourceResolve s3ResourceResolve;
    private static AboutPageResponse expectedAboutPageResponse;
    @BeforeAll
    public static void setUp(){
        expectedAboutPageResponse = new AboutPageResponse();
        expectedAboutPageResponse.setTitle("title");
        expectedAboutPageResponse.setAboutText("about text");
        expectedAboutPageResponse.setAdditionalTitle("additional title");
        expectedAboutPageResponse.setAdditionalText("additional text");
    }
    @Test
    void getAboutPage() throws Exception {
        this.mockMvc.perform(get("/web-site/about-us")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("about-us/about-us"));
    }

    @Test
    void getAboutPageResponse() throws Exception {
        when(aboutPageService.getAboutPageResponse()).thenReturn(expectedAboutPageResponse);
        this.mockMvc.perform(get("/web-site/about-us/get"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(expectedAboutPageResponse.getTitle()))
                .andExpect(jsonPath("$.aboutText").value(expectedAboutPageResponse.getAboutText()));
    }

    @Test
    void downloadDocument() throws Exception {
        when(aboutPageService.getDocument(anyString())).thenReturn(new byte[]{(byte)0xe0});
        this.mockMvc.perform(get("/web-site/about-us/download/{document}","document.doc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Disposition","attachment; filename=document.doc"));
    }
}