package com.example.controller;

import com.example.configuration.awsConfiguration.S3ResourceResolve;
import com.example.model.mainPage.MainPageResponse;
import com.example.service.ContactsService;
import com.example.service.MainPageService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = MainPageController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class MainPageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MainPageService mainPageService;
    @MockBean
    private S3ResourceResolve s3ResourceResolve;
    private static MainPageResponse expectedMainPageResponse;
    @BeforeAll
    public static void setUp(){
        expectedMainPageResponse = new MainPageResponse();
        expectedMainPageResponse.setTitle("title");
        expectedMainPageResponse.setText("text");
        expectedMainPageResponse.setImage1("image1");
        expectedMainPageResponse.setImage2("image2");
        expectedMainPageResponse.setImage3("image3");
    }

    @Test
    void getMainPage() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("main/main"));
    }

    @Test
    void getMainPageResponse() throws Exception {
        when(mainPageService.getMainPageResponse()).thenReturn(expectedMainPageResponse);
        this.mockMvc.perform(get("/get"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(expectedMainPageResponse.getTitle()))
                .andExpect(jsonPath("$.text").value(expectedMainPageResponse.getText()));
    }
}