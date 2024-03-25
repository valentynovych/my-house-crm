package com.example.controller;

import com.example.configuration.awsConfiguration.S3ResourceResolve;
import com.example.model.contactsPage.ContactsPageResponse;
import com.example.service.ContactsService;
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

@WebMvcTest(controllers = ContactsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ContactsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ContactsService contactsService;
    @MockBean
    private S3ResourceResolve s3ResourceResolve;
    private static ContactsPageResponse expectedContactsPageResponse;
    @BeforeAll
    public static void setUp(){
        expectedContactsPageResponse = new ContactsPageResponse(
                "title", "text", "link",
                "fullName", "location", "address",
                "phoneNumber", "email", "mapCode"
        );
    }
    @Test
    void getContactsPage() throws Exception {
        this.mockMvc.perform(get("/web-site/contacts")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("contacts/contacts"));
    }

    @Test
    void getContactsPageResponse() throws Exception {
        when(contactsService.getContactsPageResponse()).thenReturn(expectedContactsPageResponse);
        this.mockMvc.perform(get("/web-site/contacts/get"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(expectedContactsPageResponse.title()))
                .andExpect(jsonPath("$.fullName").value(expectedContactsPageResponse.fullName()));
    }
}