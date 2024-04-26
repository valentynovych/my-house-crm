package com.example.myhouse24admin.controller.siteManagement;

import com.example.myhouse24admin.entity.MainPage;
import com.example.myhouse24admin.entity.MainPageBlock;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageBlockRequest;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageRequest;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageResponse;
import com.example.myhouse24admin.repository.MainPageBlockRepo;
import com.example.myhouse24admin.repository.MainPageRepo;
import com.example.myhouse24admin.service.MainPageService;
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
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class MainPageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails userDetails;

    @Autowired
    private MainPageService mainPageService;
    @Autowired
    private MainPageBlockRepo mainPageBlockRepo;
    @Autowired
    private MainPageRepo mainPageRepo;

    @Test
    void getMainPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/site-management/home-page")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("site-management/main-page"));
    }

    @Test
    void getMainPageResponse() throws Exception {
        MainPageResponse mainPageResponse = new MainPageResponse();
        mainPageResponse.setText("text");
        mainPageResponse.setTitle("title");
        mainPageResponse.setImage1("image1");

        when(mainPageService.getMainPageResponse()).thenReturn(mainPageResponse);

        this.mockMvc.perform(get("/my-house/admin/site-management/home-page/get")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(mainPageResponse.getText()))
                .andExpect(jsonPath("$.title").value(mainPageResponse.getTitle()))
                .andExpect(jsonPath("$.image1").value(mainPageResponse.getImage1()));
    }

    @Test
    void updateMainPage_MainPageRequest_Valid() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        MainPageRequest mainPageRequest = new MainPageRequest();
        mainPageRequest.setTitle("title");
        mainPageRequest.setText("text");
        mainPageRequest.setTextWithoutTags("text");
        mainPageRequest.setShowLinks(true);
        mainPageRequest.setImage1(multipartFile);
        mainPageRequest.setImage2(multipartFile);
        mainPageRequest.setImage3(multipartFile);
        MainPageBlockRequest pageBlockRequest = new MainPageBlockRequest();
        pageBlockRequest.setId(1L);
        pageBlockRequest.setTitle("title");
        pageBlockRequest.setDescription("description");
        pageBlockRequest.setDescriptionWithoutTags("description");
        mainPageRequest.setMainPageBlocks(List.of(pageBlockRequest));

        MainPage mainPage = new MainPage();
        mainPage.setImage1("image1");
        mainPage.setImage2("image2");
        mainPage.setImage3("image3");

        MainPageBlock mainPageBlock = new MainPageBlock();
        mainPageBlock.setTitle("title");
        mainPageBlock.setDescription("description");
        mainPageBlock.setId(1L);


        doNothing().when(mainPageService).updateMainPage(any(MainPageRequest.class));
        doReturn(Optional.of(mainPageBlock)).when(mainPageBlockRepo).findById(anyLong());
        doReturn(Optional.of(mainPage)).when(mainPageRepo).findById(anyLong());

        this.mockMvc.perform(post("/my-house/admin/site-management/home-page")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("mainPageRequest", mainPageRequest))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void updateMainPage_MainPageRequest_Not_Valid() throws Exception {
        MockMultipartFile emptyMultipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE, new byte[0]);
        MainPageRequest mainPageRequest = new MainPageRequest();
        MainPageBlockRequest mainPageBlockRequest = new MainPageBlockRequest();
        mainPageBlockRequest.setImage(emptyMultipartFile);
        mainPageBlockRequest.setId(19L);
        mainPageRequest.setMainPageBlocks(List.of(mainPageBlockRequest));
        mainPageRequest.setImage1(emptyMultipartFile);
        mainPageRequest.setImage2(emptyMultipartFile);
        mainPageRequest.setImage3(emptyMultipartFile);

        doReturn(Optional.of(new MainPageBlock())).when(mainPageBlockRepo).findById(anyLong());
        doReturn(Optional.of(new MainPage())).when(mainPageRepo).findById(anyLong());

        this.mockMvc.perform(post("/my-house/admin/site-management/home-page")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("mainPageRequest", mainPageRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(9)));
    }
}