package com.example.myhouse24admin.controller.siteManagement;

import com.example.myhouse24admin.entity.AboutPage;
import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageRequest;
import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageResponse;
import com.example.myhouse24admin.repository.AboutPageRepo;
import com.example.myhouse24admin.service.AboutPageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

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
class AboutPageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private AboutPageService aboutPageService;
    @Autowired
    private AboutPageRepo aboutPageRepo;

    @Test
    void getAboutUsPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/site-management/about-page")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("site-management/about-page"));
    }

    @Test
    void getAboutUs() throws Exception {
        AboutPageResponse aboutPageResponse = new AboutPageResponse();
        aboutPageResponse.setTitle("title");
        aboutPageResponse.setAboutText("aboutText");
        aboutPageResponse.setDirectorImage("directorImage");

        when(aboutPageService.getAboutPageResponse()).thenReturn(aboutPageResponse);

        this.mockMvc.perform(get("/my-house/admin/site-management/about-page/get")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aboutText").value(aboutPageResponse.getAboutText()))
                .andExpect(jsonPath("$.title").value(aboutPageResponse.getTitle()))
                .andExpect(jsonPath("$.directorImage").value(aboutPageResponse.getDirectorImage()));
    }

    @Test
    void updateAboutUsPage_AboutPageRequest_Valid() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        AboutPageRequest aboutPageRequest = new AboutPageRequest();
        aboutPageRequest.setTitle("title");
        aboutPageRequest.setAboutText("text");
        aboutPageRequest.setAboutTextWithoutTags("text");
        aboutPageRequest.setDirectorImage(multipartFile);

        AboutPage aboutPage = new AboutPage();
        aboutPage.setTitle("title");
        aboutPage.setDirectorImage("image");

        doReturn(Optional.of(aboutPage)).when(aboutPageRepo).findById(anyLong());
        doNothing().when(aboutPageService).updateAboutPage(any(AboutPageRequest.class));

        this.mockMvc.perform(post("/my-house/admin/site-management/about-page")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("aboutPageRequest", aboutPageRequest))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void updateAboutUsPage_AboutPageRequest_Not_Valid() throws Exception {
        MockMultipartFile emptyMultipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE, new byte[0]);
        AboutPageRequest aboutPageRequest = new AboutPageRequest();
        aboutPageRequest.setDirectorImage(emptyMultipartFile);

        AboutPage aboutPage = new AboutPage();
        aboutPage.setTitle("title");
        aboutPage.setDirectorImage("image");

        doReturn(Optional.of(aboutPage)).when(aboutPageRepo).findById(anyLong());

        this.mockMvc.perform(post("/my-house/admin/site-management/about-page")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("aboutPageRequest", aboutPageRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(3)));
    }
}