package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.CashSheetType;
import com.example.myhouse24admin.entity.PaymentType;
import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerShortResponse;
import com.example.myhouse24admin.model.cashRegister.*;
import com.example.myhouse24admin.model.paymentItem.PaymentItemDto;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountShortResponse;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountWithApartmentOwnerResponse;
import com.example.myhouse24admin.model.staff.StaffShortResponse;
import com.example.myhouse24admin.service.CashRegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CashRegisterControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetails userDetails;
    @Autowired
    private CashRegisterService cashRegisterService;

    @BeforeEach
    void setUp() {
        clearInvocations(cashRegisterService);
    }

    @Test
    void viewCashRegisterTable() throws Exception {
        // given
        var request = get("/my-house/admin/cash-register")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("cash-register/cash-register-table")
                );
    }

    @Test
    void viewAddIncomeSheet() throws Exception {
        // given
        var request = get("/my-house/admin/cash-register/add-income-sheet")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("cash-register/add-income-sheet")
                );
    }

    @Test
    void viewEditIncomeSheet() throws Exception {
        // given
        var request = get("/my-house/admin/cash-register/edit-income-sheet/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("cash-register/edit-income-sheet")
                );
    }

    @Test
    void viewAddExpenseSheet() throws Exception {
        // given
        var request = get("/my-house/admin/cash-register/add-expense-sheet")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("cash-register/add-expense-sheet")
                );
    }

    @Test
    void viewEditExpenseSheet() throws Exception {
        // given
        var request = get("/my-house/admin/cash-register/edit-expense-sheet/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("cash-register/edit-expense-sheet")
                );
    }

    @Test
    void viewSheet() throws Exception {
        // given
        var request = get("/my-house/admin/cash-register/view-sheet/1")
                .contextPath("/my-house")
                .with(user(userDetails));

        // when
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("cash-register/view-cash-sheet")
                );
    }

    @Test
    void getSheets() throws Exception {
        // given
        var pageable = Pageable.ofSize(10);
        var cashSheetTableResponse = new CashSheetTableResponse();
        cashSheetTableResponse.setId(1L);
        cashSheetTableResponse.setAmount(BigDecimal.valueOf(100.0));
        cashSheetTableResponse.setSheetType(CashSheetType.INCOME);
        cashSheetTableResponse.setSheetNumber("00000-00001");

        var request = get("/admin/cash-register/get-sheets")
                .with(user(userDetails))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("pageSize", String.valueOf(pageable.getPageSize()));

        var cashSheetTableResponses = new PageImpl<>(
                List.of(cashSheetTableResponse, cashSheetTableResponse), pageable, 2L);

        // when
        doReturn(cashSheetTableResponses)
                .when(cashRegisterService).getSheets(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap());
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].amount").value(100.0))
                .andExpect(jsonPath("$.content[0].sheetType").value("INCOME"))
                .andExpect(jsonPath("$.content[0].sheetNumber").value("00000-00001"));

        verify(cashRegisterService, times(1))
                .getSheets(eq(pageable.getPageNumber()), eq(pageable.getPageSize()), anyMap());
    }

    @Test
    void addNewIncomeSheet() throws Exception {
        // given
        var addRequest = new CashSheetIncomeAddRequest();
        addRequest.setAmount(BigDecimal.valueOf(100.0));
        addRequest.setSheetNumber("00000-00001");
        addRequest.setCreationDate("02.02.2022");
        addRequest.setStaffId(1L);
        addRequest.setPersonalAccountId(1L);
        addRequest.setPaymentItemId(1L);
        addRequest.setProcessed(true);
        addRequest.setOwnerId(1L);
        addRequest.setComment("testComment");


        var request = post("/admin/cash-register/add-income-sheet")
                .with(user(userDetails))
                .flashAttr("addRequest", addRequest);

        // when
        doNothing()
                .when(cashRegisterService).addNewIncomeSheet(any(CashSheetIncomeAddRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());


        verify(cashRegisterService, times(1))
                .addNewIncomeSheet(any(CashSheetIncomeAddRequest.class));
    }

    @Test
    void getNextSheetNumber() throws Exception {
        // given
        var request = get("/admin/cash-register/get-next-sheet-number")
                .with(user(userDetails));

        // when
        doReturn("00000-00001")
                .when(cashRegisterService).getNextSheetNumber();
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("00000-00001"));

        verify(cashRegisterService, times(1)).getNextSheetNumber();
    }

    @Test
    void getSheetById() throws Exception {
        // given
        var sheetResponse = new CashSheetResponse();
        sheetResponse.setId(1L);
        sheetResponse.setAmount(BigDecimal.valueOf(100.0));
        sheetResponse.setSheetType(CashSheetType.INCOME);
        sheetResponse.setSheetNumber("00000-00001");

        var request = get("/admin/cash-register/get-sheet/1")
                .with(user(userDetails));

        // when
        doReturn(sheetResponse)
                .when(cashRegisterService).getSheetById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.sheetType").value("INCOME"))
                .andExpect(jsonPath("$.sheetNumber").value("00000-00001"));

        verify(cashRegisterService, times(1)).getSheetById(eq(1L));
    }

    @Test
    void updateIncomeSheetById() throws Exception {
        // given
        var updateRequest = new CashSheetIncomeUpdateRequest();
        updateRequest.setId(1L);
        updateRequest.setSheetNumber("00000-00001");
        updateRequest.setCreationDate("02.02.2022");
        updateRequest.setOwnerId(1L);
        updateRequest.setPersonalAccountId(1L);
        updateRequest.setPaymentItemId(1L);
        updateRequest.setAmount(BigDecimal.valueOf(100.0));
        updateRequest.setProcessed(true);
        updateRequest.setStaffId(1L);
        updateRequest.setComment("testComment");

        var request = post("/admin/cash-register/edit-income-sheet/1")
                .with(user(userDetails))
                .flashAttr("updateRequest", updateRequest);

        // when
        doNothing()
                .when(cashRegisterService).updateSheetById(eq(1L), any(CashSheetIncomeUpdateRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());


        verify(cashRegisterService, times(1))
                .updateSheetById(eq(1L), any(CashSheetIncomeUpdateRequest.class));
    }

    @Test
    void addNewExpenseSheet() throws Exception {
        // given
        var addRequest = new CashSheetExpenseAddRequest();
        addRequest.setSheetNumber("00000-00001");
        addRequest.setCreationDate("02.02.2022");
        addRequest.setPaymentItemId(1L);
        addRequest.setAmount(BigDecimal.valueOf(100.0));
        addRequest.setProcessed(true);
        addRequest.setStaffId(1L);
        addRequest.setComment("testComment");

        var request = post("/admin/cash-register/add-expense-sheet")
                .with(user(userDetails))
                .flashAttr("addRequest", addRequest);

        // when
        doNothing()
                .when(cashRegisterService).addNewExpenseSheet(any(CashSheetExpenseAddRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());


        verify(cashRegisterService, times(1))
                .addNewExpenseSheet(any(CashSheetExpenseAddRequest.class));
    }

    @Test
    void updateExpenseSheetById() throws Exception {
        // given
        var updateRequest = new CashSheetExpenseUpdateRequest();
        updateRequest.setId(1L);
        updateRequest.setSheetNumber("00000-00001");
        updateRequest.setCreationDate("02.02.2022");
        updateRequest.setPaymentItemId(1L);
        updateRequest.setAmount(BigDecimal.valueOf(100.0));
        updateRequest.setProcessed(true);
        updateRequest.setStaffId(1L);
        updateRequest.setComment("testComment");

        var request = post("/admin/cash-register/edit-expense-sheet/1")
                .with(user(userDetails))
                .flashAttr("updateRequest", updateRequest);

        // when
        doNothing()
                .when(cashRegisterService).updateSheetById(eq(1L), any(CashSheetExpenseUpdateRequest.class));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());


        verify(cashRegisterService, times(1))
                .updateSheetById(eq(1L), any(CashSheetExpenseUpdateRequest.class));
    }

    @Test
    void exportToExcel() throws Exception {
        // given
        var sheetResponse = new CashSheetResponse();
        sheetResponse.setSheetNumber("00000-00001");
        sheetResponse.setCreationDate(Instant.now());
        sheetResponse.setPersonalAccount(new PersonalAccountWithApartmentOwnerResponse(
                1L,
                1L,
                new ApartmentOwnerShortResponse(1L, "testOwner", "+38050000000")
        ));
        PaymentItemDto paymentItem = new PaymentItemDto();
        paymentItem.setName("testPaymentItem");
        sheetResponse.setPaymentItem(paymentItem);
        StaffShortResponse staff = new StaffShortResponse();
        staff.setFirstName("testFirstName");
        staff.setLastName("testLastName");
        sheetResponse.setStaff(staff);
        sheetResponse.setAmount(BigDecimal.valueOf(100.0));
        sheetResponse.setProcessed(true);
        sheetResponse.setComment("testComment");

        var request = get("/admin/cash-register/export-view-to-exel/1")
                .with(user(userDetails));

        // when
        doReturn(sheetResponse)
                .when(cashRegisterService).getSheetById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=CashSheet_00000-00001.xlsx"));

        verify(cashRegisterService, times(1))
                .getSheetById(eq(1L));
    }

    @Test
    void exportTableToExcel() throws Exception {
        // given
        var searchParams = new HashMap<String, String>();
        searchParams.put("accountNumber", "1");

        var tableResponse = new CashSheetTableResponse();
        tableResponse.setSheetNumber("00000-00001");
        tableResponse.setCreationDate(Instant.now());
        tableResponse.setProcessed(true);
        tableResponse.setSheetType(CashSheetType.INCOME);
        PaymentItemDto paymentItem = new PaymentItemDto();
        paymentItem.setName("Test payment item");
        paymentItem.setPaymentType(PaymentType.INCOME);
        tableResponse.setPaymentItem(paymentItem);
        tableResponse.setApartmentOwner(
                new ApartmentOwnerShortResponse(1L, "Test Full Name", "+380123456789"));
        PersonalAccountShortResponse personalAccount = new PersonalAccountShortResponse();
        personalAccount.setAccountNumber(1L);
        personalAccount.setAccountNumber(1L);
        tableResponse.setAmount(BigDecimal.valueOf(100.0));
        tableResponse.setPersonalAccount(personalAccount);

        var request = get("/admin/cash-register/export-table-to-exel")
                .with(user(userDetails))
                .requestAttr("searchParams", searchParams)
                .param("page", "0")
                .param("pageSize", "10");

        var cashSheetTableResponses = new PageImpl<>(
                List.of(tableResponse, tableResponse), Pageable.ofSize(10), 2L);

        // when
        doReturn(cashSheetTableResponses)
                .when(cashRegisterService).getSheets(eq(0), eq(10), anyMap());
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(header().exists("Content-Disposition"));

        verify(cashRegisterService, times(1))
                .getSheets(eq(0), eq(10), anyMap());

    }

    @Test
    void deleteCashSheet_WhenSuccessDelete() throws Exception {
        // given
        var request = delete("/admin/cash-register/delete-sheet/1")
                .with(user(userDetails));

        // when
        doReturn("Success")
                .when(cashRegisterService).deleteCashSheetById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isOk());

        verify(cashRegisterService, times(1)).deleteCashSheetById(eq(1L));
    }

    @Test
    void deleteCashSheet_WhenDeleteIsFail() throws Exception {
        // given
        var request = delete("/admin/cash-register/delete-sheet/1")
                .with(user(userDetails));

        // when
        doReturn("Delete is fail")
                .when(cashRegisterService).deleteCashSheetById(eq(1L));
        this.mockMvc.perform(request)

                // then
                .andDo(print())
                .andExpect(status().isLocked());

        verify(cashRegisterService, times(1)).deleteCashSheetById(eq(1L));
    }
}