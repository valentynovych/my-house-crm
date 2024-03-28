package com.example.myhouse24user.controller;

import com.example.myhouse24user.entity.InvoiceStatus;
import com.example.myhouse24user.model.invoice.InvoiceItemResponse;
import com.example.myhouse24user.model.invoice.InvoiceResponse;
import com.example.myhouse24user.model.invoice.ViewInvoiceResponse;
import com.example.myhouse24user.service.InvoiceService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class InvoiceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @MockBean
    private InvoiceService invoiceService;
    private static InvoiceResponse expectedInvoiceResponse;
    private static ViewInvoiceResponse expectedViewInvoiceResponse;
    @BeforeAll
    public static void setUp() {
        expectedInvoiceResponse = new InvoiceResponse(1L, "001", "12.09.1900",
                InvoiceStatus.PAID, BigDecimal.valueOf(22), BigDecimal.valueOf(45));

        expectedViewInvoiceResponse = new ViewInvoiceResponse();
        expectedViewInvoiceResponse.setNumber("number");
        expectedViewInvoiceResponse.setTotalPrice(BigDecimal.valueOf(32));
        expectedViewInvoiceResponse.setInvoiceItemResponses(List.of(new InvoiceItemResponse()));

    }
    @Test
    void getInvoicesPage() throws Exception {
        this.mockMvc.perform(get("/cabinet/invoices").with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/invoices"));
    }

    @Test
    void getInvoices() throws Exception {
        Pageable pageable = PageRequest.of(0,1);
        Map<String, String> requestMap = new HashMap<>();

        when(invoiceService.getInvoiceResponses(anyMap()))
                .thenReturn(new PageImpl<>(List.of(expectedInvoiceResponse), pageable, 5));

        this.mockMvc.perform(get("/cabinet/invoices/get")
                        .with(user(userDetails))
                        .param("requestMap", String.valueOf(requestMap)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size",is(1)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].creationDate").value(expectedInvoiceResponse.creationDate()))
                .andExpect(jsonPath("$.content[0].number").value(expectedInvoiceResponse.number()));
    }

    @Test
    void getStatuses() throws Exception {
        this.mockMvc.perform(get("/cabinet/invoices/get-statuses")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(InvoiceStatus.PAID.toString()))
                .andExpect(jsonPath("$.[1]").value(InvoiceStatus.UNPAID.toString()))
                .andExpect(jsonPath("$.[2]").value(InvoiceStatus.PARTLY_PAID.toString()));
    }

    @Test
    void getInvoicesForApartmentPage() throws Exception {
        this.mockMvc.perform(get("/cabinet/invoices/{id}",1L)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/invoices"));
    }

    @Test
    void getViewInvoicePage() throws Exception {
        this.mockMvc.perform(get("/cabinet/invoices/view-invoice/{id}",1L)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/view-invoice"));
    }

    @Test
    void getViewInvoice() throws Exception {
        when(invoiceService.getViewInvoiceResponse(anyLong()))
                .thenReturn(expectedViewInvoiceResponse);
        this.mockMvc.perform(get("/cabinet/invoices/view-invoice/get/{id}",
                        1L).with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(expectedViewInvoiceResponse.getNumber()))
                .andExpect(jsonPath("$.totalPrice").value(expectedViewInvoiceResponse.getTotalPrice()));

    }

    @Test
    void downloadInPdf() throws Exception {
        when(invoiceService.createPdfFile(anyLong())).thenReturn(new byte[]{(byte)0xe0});
        this.mockMvc.perform(get("/cabinet/invoices/view-invoice/download-in-pdf/{id}",
                        1L).with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Disposition",
                        "attachment; filename="+"invoice_"+ LocalDate.now()+".pdf"));

    }
}