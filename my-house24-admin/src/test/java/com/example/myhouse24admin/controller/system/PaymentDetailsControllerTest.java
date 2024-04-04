package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.model.paymentDetails.PaymentDetailsDto;
import com.example.myhouse24admin.service.PaymentDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PaymentDetailsService paymentDetailsService;
    @Autowired
    private UserDetails userDetails;
    private static PaymentDetailsDto paymentDetailsDto;

    @BeforeEach
    void setUp() {
        clearInvocations(paymentDetailsService);

        paymentDetailsDto = new PaymentDetailsDto(
                1L,
                "testCompanyName",
                "testCompanyDetails"
        );
    }

    @Test
    void viewPaymentDetails() throws Exception {
        // given
        var request = get("/admin/system-settings/payment-details")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("system/payment.details/payment-details")
                );
    }

    @Test
    void getPaymentDetails() throws Exception {
        // given
        var request = get("/admin/system-settings/payment-details/get-details")
                .with(user(userDetails));

        // when
        when(paymentDetailsService.getPaymentDetails())
                .thenReturn(paymentDetailsDto);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "id": 1, "companyName": "testCompanyName", "companyDetails": "testCompanyDetails"
                                }
                                """)
                );
        verify(paymentDetailsService, times(1)).getPaymentDetails();
    }

    @Test
    void updatePaymentDetails() throws Exception {
        // given
        var request = post("/admin/system-settings/payment-details/update-details")
                .with(user(userDetails))
                .flashAttr("paymentDetailsDto", paymentDetailsDto);

        // when
        doNothing().when(paymentDetailsService).updatePaymentDetails(eq(paymentDetailsDto));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk()
                );
        verify(paymentDetailsService, times(1)).updatePaymentDetails(eq(paymentDetailsDto));
    }
}