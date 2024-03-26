package com.example.myhouse24user.controller;

import com.example.myhouse24user.configuration.awsConfiguration.S3ResourceResolve;
import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.InvoiceStatus;
import com.example.myhouse24user.model.invoice.InvoiceItemResponse;
import com.example.myhouse24user.model.invoice.InvoiceResponse;
import com.example.myhouse24user.model.invoice.ViewInvoiceResponse;
import com.example.myhouse24user.model.owner.ApartmentOwnerDetails;
import com.example.myhouse24user.securityFilter.RecaptchaFilter;
import com.example.myhouse24user.service.InvoiceService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InvoiceController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InvoiceService invoiceService;
    @MockBean
    private S3ResourceResolve s3ResourceResolve;
    @MockBean
    private RecaptchaFilter recaptchaFilter;
    private static InvoiceResponse expectedInvoiceResponse;
    private static ViewInvoiceResponse expectedViewInvoiceResponse;
    private static ApartmentOwnerDetails apartmentOwnerDetails;
    @BeforeAll
    public static void setUp() {
        expectedInvoiceResponse = new InvoiceResponse(1L, "001", "12.09.1900",
                InvoiceStatus.PAID, BigDecimal.valueOf(22), BigDecimal.valueOf(45));

        expectedViewInvoiceResponse = new ViewInvoiceResponse();
        expectedViewInvoiceResponse.setNumber("number");
        expectedViewInvoiceResponse.setTotalPrice(BigDecimal.valueOf(32));
        expectedViewInvoiceResponse.setInvoiceItemResponses(List.of(new InvoiceItemResponse()));

        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setEmail("email");
        apartmentOwner.setApartments(List.of());
        apartmentOwnerDetails = new ApartmentOwnerDetails(apartmentOwner);
    }
    @Test
    void getInvoicesPage() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(apartmentOwnerDetails);
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/cabinet/invoices")).andDo(print())
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
        this.mockMvc.perform(get("/cabinet/invoices/get-statuses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(InvoiceStatus.PAID.toString()))
                .andExpect(jsonPath("$.[1]").value(InvoiceStatus.UNPAID.toString()))
                .andExpect(jsonPath("$.[2]").value(InvoiceStatus.PARTLY_PAID.toString()));
    }

    @Test
    void getInvoicesForApartmentPage() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(apartmentOwnerDetails);
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/cabinet/invoices/{id}",1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/invoices"));
    }

    @Test
    void getViewInvoicePage() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getPrincipal()).thenReturn(apartmentOwnerDetails);
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/cabinet/invoices/view-invoice/{id}",1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/view-invoice"));
    }

    @Test
    void getViewInvoice() throws Exception {
        when(invoiceService.getViewInvoiceResponse(anyLong()))
                .thenReturn(expectedViewInvoiceResponse);
        this.mockMvc.perform(get("/cabinet/invoices/view-invoice/get/{id}",
                        1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(expectedViewInvoiceResponse.getNumber()))
                .andExpect(jsonPath("$.totalPrice").value(expectedViewInvoiceResponse.getTotalPrice()));

    }

    @Test
    void downloadInPdf() throws Exception {
        when(invoiceService.createPdfFile(anyLong())).thenReturn(new byte[]{(byte)0xe0});
        this.mockMvc.perform(get("/cabinet/invoices/view-invoice/download-in-pdf/{id}",
                        1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Disposition",
                        "attachment; filename="+"invoice_"+ LocalDate.now()+".pdf"));

    }
}