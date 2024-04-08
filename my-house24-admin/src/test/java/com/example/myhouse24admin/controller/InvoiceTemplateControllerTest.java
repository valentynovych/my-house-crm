package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateListRequest;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateRequest;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateResponse;
import com.example.myhouse24admin.service.InvoiceTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class InvoiceTemplateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails userDetails;

    @Autowired
    private InvoiceTemplateService invoiceTemplateService;

    @Test
    void getTemplatesSettingsPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/invoices/templates-settings")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/templates-settings"));
    }

    @Test
    void getInvoiceTemplates() throws Exception {
        InvoiceTemplateResponse invoiceTemplateResponse = new InvoiceTemplateResponse(1L,
                "name", "file", true);

        when(invoiceTemplateService.getInvoiceTemplatesResponses())
                .thenReturn(List.of(invoiceTemplateResponse));

        this.mockMvc.perform(get("/my-house/admin/invoices/templates-settings/get")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(invoiceTemplateResponse.id()))
                .andExpect(jsonPath("$.[0].file").value(invoiceTemplateResponse.file()))
                .andExpect(jsonPath("$.[0].name").value(invoiceTemplateResponse.name()));

    }

    @Test
    void updateInvoiceTemplates_InvoiceTemplateListRequest_Valid() throws Exception {
        InvoiceTemplateListRequest invoiceTemplateListRequest = new InvoiceTemplateListRequest();
        InvoiceTemplateRequest invoiceTemplateRequest = new InvoiceTemplateRequest();
        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,"some text".getBytes());
        invoiceTemplateRequest.setFile(multipartFile);
        invoiceTemplateRequest.setName("name");
        invoiceTemplateListRequest.setInvoiceTemplates(List.of(invoiceTemplateRequest));

        doNothing().when(invoiceTemplateService).updateTemplates(any(InvoiceTemplateListRequest.class));

        this.mockMvc.perform(post("/my-house/admin/invoices/templates-settings")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("invoiceTemplateListRequest", invoiceTemplateListRequest))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void updateInvoiceTemplates_InvoiceTemplateListRequest_Not_Valid() throws Exception {
        InvoiceTemplateListRequest invoiceTemplateListRequest = new InvoiceTemplateListRequest();
        InvoiceTemplateRequest invoiceTemplateRequest = new InvoiceTemplateRequest();
        MockMultipartFile multipartFile = new MockMultipartFile("mainImage","file.jpg", MediaType.TEXT_PLAIN_VALUE,new byte[0]);
        invoiceTemplateRequest.setFile(multipartFile);
        invoiceTemplateRequest.setName("");
        invoiceTemplateListRequest.setInvoiceTemplates(List.of(invoiceTemplateRequest));

        this.mockMvc.perform(post("/my-house/admin/invoices/templates-settings")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .flashAttr("invoiceTemplateListRequest", invoiceTemplateListRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.size()", is(2)));

    }
    @Test
    void setDefaultInvoice() throws Exception {
        doNothing().when(invoiceTemplateService).setDefaultInvoice(anyLong());

        this.mockMvc.perform(post("/my-house/admin/invoices/templates-settings/set-default/{id}",1)
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void downloadTemplate() throws Exception {
        when(invoiceTemplateService.getTemplateFile(anyString()))
                .thenReturn(new byte[]{0x01});

        this.mockMvc.perform(get("/my-house/admin/invoices/templates-settings/download-template/{fileName}","template.xsl")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Disposition",
                        "attachment; filename=template.xsl"));
    }
}