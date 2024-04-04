package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.entity.PaymentType;
import com.example.myhouse24admin.model.paymentItem.PaymentItemDto;
import com.example.myhouse24admin.service.PaymentItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private PaymentItemService paymentItemService;
    private static PaymentItemDto paymentItemDto;


    @BeforeEach
    void setUp() {
        clearInvocations(paymentItemService);
        paymentItemDto = new PaymentItemDto();
        paymentItemDto.setId(1L);
        paymentItemDto.setName("testName");
        paymentItemDto.setDeleted(false);
        paymentItemDto.setPaymentType(PaymentType.INCOME);
    }

    @Test
    void viewPaymentItems() throws Exception {
        // given
        var request = get("/admin/system-settings/payment-items")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("system/payment.item/payment-items")
                );
    }

    @Test
    void editItemById() throws Exception {
        // given
        var request = get("/admin/system-settings/payment-items/edit-item/1")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("system/payment.item/edit-payment-item")
                );
    }

    @Test
    void testEditItemById() throws Exception {
        // given
        var request = get("/admin/system-settings/payment-items/add-item")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("system/payment.item/add-payment-item")
                );
    }

    @Test
    void getAllItems() throws Exception {
        // given
        var pageable = PageRequest.of(0, 10);
        var searchParams = new HashMap<String, String>();
        searchParams.put("page", "0");
        var request = get("/admin/system-settings/payment-items/get-items")
                .with(user(userDetails))
                .param("page", "0")
                .param("pageSize", "10");

        var paymentItemPage = new PageImpl<>(List.of(paymentItemDto, paymentItemDto, paymentItemDto), pageable, 3);

        // when
        when(paymentItemService.getPaymentItems(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap()))
                .thenReturn(paymentItemPage);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "content": [
                                {
                                    "id": 1, "name": "testName", "deleted": false, "paymentType": "INCOME"
                                },
                                {
                                    "id": 1, "name": "testName", "deleted": false, "paymentType": "INCOME"
                                },
                                {
                                    "id": 1, "name": "testName", "deleted": false, "paymentType": "INCOME"
                                }
                            ], "totalElements": 3
                        }
                        """)
                );
        verify(paymentItemService, times(1))
                .getPaymentItems(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap());
    }

    @Test
    void getItemById() throws Exception {
        // given
        var request = get("/admin/system-settings/payment-items/get-item/1")
                .with(user(userDetails));
        // when
        when(paymentItemService.getItemById(eq(1L)))
                .thenReturn(paymentItemDto);
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                                {
                                    "id": 1, "name": "testName", "deleted": false, "paymentType": "INCOME"
                               }
                        """)
                );
        verify(paymentItemService, times(1)).getItemById(eq(1L));
    }

    @Test
    void testEditItemById1_WhenRequestIsNotValid() throws Exception {
        // given
        var request = post("/admin/system-settings/payment-items/edit-item/1")
                .with(user(userDetails))
                .flashAttr("paymentItem", new PaymentItemDto());

        // when
        doNothing().when(paymentItemService).editItemById(eq(1L), any(PaymentItemDto.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(paymentItemService, never()).editItemById(eq(1L), any(PaymentItemDto.class));
    }

    @Test
    void testEditItemById1_WhenRequestIsValid() throws Exception {
        // given
        var request = post("/admin/system-settings/payment-items/edit-item/1")
                .with(user(userDetails))
                .flashAttr("paymentItem", paymentItemDto);

        // when
        doNothing().when(paymentItemService).editItemById(eq(1L), eq(paymentItemDto));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());
        verify(paymentItemService, times(1)).editItemById(eq(1L), eq(paymentItemDto));
    }

    @Test
    void addItem() throws Exception {
        // given
        var request = post("/admin/system-settings/payment-items/add-item")
                .with(user(userDetails))
                .flashAttr("paymentItem", paymentItemDto);

        // when
        doNothing().when(paymentItemService).addItem(eq(paymentItemDto));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());
        verify(paymentItemService, times(1)).addItem(eq(paymentItemDto));

    }

    @Test
    void getAllItemTypes() throws Exception {
        // given
        var request = get("/admin/system-settings/payment-items/get-item-types")
                .with(user(userDetails));

        var itemTypesLength = PaymentType.values().length;
        // when
        doReturn(Map.of("INCOME", "Доход", "EXPENSE", "Расход"))
                .when(paymentItemService).getItemTypes();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(itemTypesLength));
        verify(paymentItemService, times(1)).getItemTypes();
    }

    @Test
    void deletePaymentItem_WhenSuccessDeleted() throws Exception {
        // given
        var request = delete("/admin/system-settings/payment-items/delete/1")
                .with(user(userDetails));

        // when
        doReturn(true)
                .when(paymentItemService).deleteItemById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());
        verify(paymentItemService, times(1)).deleteItemById(eq(1L));
    }

    @Test
    void deletePaymentItem_WhenFailDeleted() throws Exception {
        // given
        var request = delete("/admin/system-settings/payment-items/delete/1")
                .with(user(userDetails));

        // when
        doReturn(false)
                .when(paymentItemService).deleteItemById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(paymentItemService, times(1)).deleteItemById(eq(1L));
    }
}