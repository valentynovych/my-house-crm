package com.example.myhouse24admin.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails userDetails;

    @Test
    void getInvoicesPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/invoices").contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/invoices"));
    }

    @Test
    void getInvoices() {
    }

    @Test
    void getInvoicePage() {
    }

    @Test
    void createInvoice() {
    }

    @Test
    void getStatuses() {
    }

    @Test
    void getHouses() {
    }

    @Test
    void getSections() {
    }

    @Test
    void getApartments() {
    }

    @Test
    void getServices() {
    }

    @Test
    void getNumber() {
    }

    @Test
    void getOwner() {
    }

    @Test
    void getMeterReadings() {
    }

    @Test
    void getTariffItems() {
    }

    @Test
    void getUnitOfMeasurement() {
    }

    @Test
    void getAmountOfConsumptions() {
    }

    @Test
    void getOwners() {
    }

    @Test
    void getEditInvoicePage() {
    }

    @Test
    void getInvoice() {
    }

    @Test
    void updateInvoice() {
    }

    @Test
    void getViewInvoicePage() {
    }

    @Test
    void getInvoiceForView() {
    }

    @Test
    void deleteInvoice() {
    }

    @Test
    void deleteInvoices() {
    }

    @Test
    void getInvoicePageForCopy() {
    }

    @Test
    void saveCopiedInvoice() {
    }

    @Test
    void getPrintTemplatePage() {
    }

    @Test
    void downloadInvoice() {
    }

    @Test
    void getNumberById() {
    }

    @Test
    void sendInvoice() {
    }
}