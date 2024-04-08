package com.example.myhouse24admin.controller.siteManagement;

import com.example.myhouse24admin.model.siteManagement.contacts.ContactsPageDto;
import com.example.myhouse24admin.service.ContactsPageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

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
class ContactsPageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails userDetails;

    @Autowired
    private ContactsPageService contactsPageService;
    private ContactsPageDto contactsPageDto = new ContactsPageDto("title", "text",
            "link", "name", "location", "address",
            "+380993456789", "email@gmail.com", "map", "seo",
            "description", "keywords");

    @Test
    void getContactsPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/site-management/contacts-page")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("site-management/contacts"));
    }

    @Test
    void getContacts() throws Exception {

        when(contactsPageService.getContactsPageDto()).thenReturn(contactsPageDto);

        this.mockMvc.perform(get("/my-house/admin/site-management/contacts-page/get")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(contactsPageDto.text()))
                .andExpect(jsonPath("$.title").value(contactsPageDto.title()))
                .andExpect(jsonPath("$.email").value(contactsPageDto.email()));
    }

    @Test
    void updateContactsPage_ContactsPageDto_Valid() throws Exception {
        doNothing().when(contactsPageService).updateContactsPage(any(ContactsPageDto.class));

        this.mockMvc.perform(post("/my-house/admin/site-management/contacts-page")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("contactsPageDto", contactsPageDto))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void updateContactsPage_ContactsPageDto_Not_Valid() throws Exception {
        ContactsPageDto notValidContactsPageDto = new ContactsPageDto("", "",
                "", "", "", "", "",
                "", "", "", "", "");

        this.mockMvc.perform(post("/my-house/admin/site-management/contacts-page")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("contactsPageDto", notValidContactsPageDto))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(9)));
    }
}