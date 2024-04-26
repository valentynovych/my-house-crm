package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.mapper.CashSheetMapper;
import com.example.myhouse24admin.model.cashRegister.*;
import com.example.myhouse24admin.repository.CashSheetRepo;
import com.example.myhouse24admin.specification.CashSheetSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashRegisterServiceImplTest {

    @Mock
    private CashSheetRepo cashSheetRepo;
    @Mock
    private CashSheetMapper cashSheetMapper;
    @InjectMocks
    private CashRegisterServiceImpl cashRegisterService;
    private static CashSheet cashSheet;

    @BeforeEach
    void setUp() {
        cashSheet = new CashSheet();
        cashSheet.setId(1L);
        cashSheet.setSheetNumber("0000000001");
        cashSheet.setProcessed(false);
        cashSheet.setDeleted(false);
        cashSheet.setComment("comment");
        cashSheet.setInvoice(null);
        PaymentItem paymentItem = new PaymentItem();
        paymentItem.setId(1L);
        paymentItem.setName("paymentItem");
        paymentItem.setPaymentType(PaymentType.INCOME);
        cashSheet.setPaymentItem(paymentItem);
        cashSheet.setCreationDate(Instant.now());
        cashSheet.setSheetType(CashSheetType.INCOME);
        cashSheet.setPersonalAccount(null);
    }

    @Test
    void getSheets() {
        // given
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("sheetNumber", "0000000001");

        List<CashSheet> cashSheets = List.of(cashSheet, cashSheet, cashSheet);
        Page<CashSheet> cashSheetPage = new PageImpl<>(cashSheets, PageRequest.of(0, 10), cashSheets.size());

        List<CashSheetTableResponse> responseList = List.of(new CashSheetTableResponse(), new CashSheetTableResponse(),
                new CashSheetTableResponse());
        // when
        when(cashSheetRepo.findAll(any(CashSheetSpecification.class), any(Pageable.class)))
                .thenReturn(cashSheetPage);
        when(cashSheetMapper.sashSheetListToCashSheetTableResponseList(cashSheets))
                .thenReturn(responseList);

        // call the method
        Page<CashSheetTableResponse> responses = cashRegisterService.getSheets(0, 10, searchParams);

        // then
        assertFalse(responses.isEmpty());
        assertEquals(3, responses.getNumberOfElements());

        verify(cashSheetRepo, times(1)).findAll(any(CashSheetSpecification.class), any(Pageable.class));
        verify(cashSheetMapper, times(1)).sashSheetListToCashSheetTableResponseList(cashSheets);
    }

    @Test
    void addNewIncomeSheet() {
        // given
        CashSheetIncomeAddRequest addRequest = new CashSheetIncomeAddRequest();
        addRequest.setComment("comment");
        addRequest.setAmount(BigDecimal.valueOf(100.0));
        addRequest.setCreationDate("2022.01.01");
        addRequest.setOwnerId(1L);
        addRequest.setPaymentItemId(1L);
        addRequest.setStaffId(1L);

        // when
        when(cashSheetMapper.cashSheetIncomeAddRequestToCashSheet(addRequest))
                .thenReturn(cashSheet);
        when(cashSheetRepo.getMaxId()).thenReturn(1L);
        when(cashSheetRepo.save(cashSheet))
                .thenReturn(cashSheet);

        // call the method
        cashRegisterService.addNewIncomeSheet(addRequest);

        // then

        verify(cashSheetMapper, times(1)).cashSheetIncomeAddRequestToCashSheet(addRequest);
        verify(cashSheetRepo, times(1)).save(cashSheet);
    }

    @Test
    void getNextSheetNumber() {
        // when
        when(cashSheetRepo.getMaxId())
                .thenReturn(1L);

        // call the method
        String nextSheetNumber = cashRegisterService.getNextSheetNumber();

        // then
        assertEquals("0000000001", nextSheetNumber);
    }

    @Test
    void getSheetById() {
        // given
        Long sheetId = 1L;
        CashSheetResponse sheetResponse = new CashSheetResponse();

        // when
        when(cashSheetRepo.findCashSheetByIdAndDeletedIsFalse(eq(sheetId)))
                .thenReturn(Optional.of(cashSheet));
        when(cashSheetMapper.cashSheetToCashSheetWithOwnerResponse(cashSheet))
                .thenReturn(sheetResponse);

        // call the method
        CashSheetResponse response = cashRegisterService.getSheetById(sheetId);

        // then
        assertEquals(sheetResponse, response);

        verify(cashSheetRepo, times(1)).findCashSheetByIdAndDeletedIsFalse(eq(sheetId));
        verify(cashSheetMapper, times(1)).cashSheetToCashSheetWithOwnerResponse(cashSheet);
    }

    @Test
    void getSheetById_whenSheetNotFound() {
        // given
        Long sheetId = 1L;

        // when
        when(cashSheetRepo.findCashSheetByIdAndDeletedIsFalse(eq(sheetId)))
                .thenReturn(Optional.empty());

        // call the method
        assertThrows(EntityNotFoundException.class, () -> cashRegisterService.getSheetById(sheetId));

        // then
        verify(cashSheetRepo, times(1)).findCashSheetByIdAndDeletedIsFalse(eq(sheetId));
    }

    @Test
    void updateSheetById_WithoutInvoice() {
        // given
        Long sheetId = cashSheet.getId();
        CashSheetIncomeUpdateRequest updateRequest = new CashSheetIncomeUpdateRequest();
        updateRequest.setComment("comment");
        updateRequest.setAmount(BigDecimal.valueOf(100.0));
        updateRequest.setCreationDate("2022.01.01");
        updateRequest.setOwnerId(1L);
        updateRequest.setPaymentItemId(1L);
        updateRequest.setStaffId(1L);

        // when
        when(cashSheetRepo.findCashSheetByIdAndDeletedIsFalse(eq(sheetId)))
                .thenReturn(Optional.of(cashSheet));
        doNothing().when(cashSheetMapper).updateCashSheetFromCashSheetIncomeUpdateRequest(cashSheet, updateRequest);
        when(cashSheetRepo.save(cashSheet))
                .thenReturn(cashSheet);

        // call the method
        cashRegisterService.updateSheetById(sheetId, updateRequest);

        // then
        verify(cashSheetRepo, times(1)).findCashSheetByIdAndDeletedIsFalse(eq(sheetId));
        verify(cashSheetMapper, times(1)).updateCashSheetFromCashSheetIncomeUpdateRequest(cashSheet, updateRequest);
        verify(cashSheetRepo, times(1)).save(cashSheet);

    }

    @Test
    void updateSheetById_WithInvoice() {
        // given
        cashSheet.setInvoice(new Invoice());
        cashSheet.setProcessed(true);
        Long sheetId = 1L;
        CashSheetIncomeUpdateRequest updateRequest = new CashSheetIncomeUpdateRequest();
        updateRequest.setComment("comment");
        updateRequest.setAmount(BigDecimal.valueOf(100.0));
        updateRequest.setCreationDate("2022.01.01");
        updateRequest.setOwnerId(1L);
        updateRequest.setPaymentItemId(1L);
        updateRequest.setStaffId(1L);
        updateRequest.setProcessed(true);

        ArgumentCaptor<CashSheet> argumentCaptor = ArgumentCaptor.forClass(CashSheet.class);

        // when
        when(cashSheetRepo.findCashSheetByIdAndDeletedIsFalse(eq(sheetId)))
                .thenReturn(Optional.of(cashSheet));
        doNothing().when(cashSheetMapper).updateCashSheetFromCashSheetIncomeUpdateRequest(cashSheet, updateRequest);
        when(cashSheetRepo.save(cashSheet))
                .thenReturn(cashSheet);

        // call the method
        cashRegisterService.updateSheetById(sheetId, updateRequest);

        // then
        verify(cashSheetRepo, times(1)).findCashSheetByIdAndDeletedIsFalse(eq(sheetId));
        verify(cashSheetMapper, times(1)).updateCashSheetFromCashSheetIncomeUpdateRequest(cashSheet, updateRequest);
        verify(cashSheetRepo, times(1)).save(argumentCaptor.capture());

        CashSheet savedCashSheet = argumentCaptor.getValue();
        assertTrue(savedCashSheet.isProcessed());
        assertTrue(savedCashSheet.getInvoice().isProcessed());

    }

    @Test
    void addNewExpenseSheet() {
        // given
        CashSheetExpenseAddRequest addRequest = new CashSheetExpenseAddRequest();
        addRequest.setComment("comment");
        addRequest.setAmount(BigDecimal.valueOf(100.0));
        addRequest.setCreationDate("2022.01.01");
        addRequest.setPaymentItemId(1L);
        addRequest.setStaffId(1L);

        // when
        when(cashSheetMapper.cashSheetExpenseAddRequestToCashSheet(addRequest))
                .thenReturn(cashSheet);
        when(cashSheetRepo.getMaxId()).thenReturn(1L);
        when(cashSheetRepo.save(cashSheet))
                .thenReturn(cashSheet);

        // call the method
        cashRegisterService.addNewExpenseSheet(addRequest);

        // then
        verify(cashSheetMapper, times(1)).cashSheetExpenseAddRequestToCashSheet(addRequest);
        verify(cashSheetRepo, times(1)).save(cashSheet);
    }

    @Test
    void testUpdateSheetById() {
        // given
        Long sheetId = 1L;
        CashSheetExpenseUpdateRequest updateRequest = new CashSheetExpenseUpdateRequest();
        updateRequest.setComment("comment");
        updateRequest.setAmount(BigDecimal.valueOf(100.0));
        updateRequest.setCreationDate("2022.01.01");
        updateRequest.setPaymentItemId(1L);
        updateRequest.setStaffId(1L);

        // when
        when(cashSheetRepo.findCashSheetByIdAndDeletedIsFalse(eq(sheetId)))
                .thenReturn(Optional.of(cashSheet));
        doNothing().when(cashSheetMapper).updateCashSheetFromCashSheetExpenseUpdateRequest(cashSheet, updateRequest);
        when(cashSheetRepo.save(cashSheet))
                .thenReturn(cashSheet);

        // call the method
        cashRegisterService.updateSheetById(sheetId, updateRequest);

        // then
        verify(cashSheetRepo, times(1)).findCashSheetByIdAndDeletedIsFalse(eq(sheetId));
        verify(cashSheetMapper, times(1)).updateCashSheetFromCashSheetExpenseUpdateRequest(cashSheet, updateRequest);
    }

    @Test
    void deleteCashSheetById_WhenSheetIsNotProcessed() {
        // given
        Long sheetId = 1L;
        cashSheet.setProcessed(false);
        ArgumentCaptor<CashSheet> argumentCaptor = ArgumentCaptor.forClass(CashSheet.class);

        // when
        when(cashSheetRepo.findCashSheetByIdAndDeletedIsFalse(eq(sheetId)))
                .thenReturn(Optional.of(cashSheet));
        when(cashSheetRepo.save(cashSheet))
                .thenReturn(cashSheet);

        // call the method
        String string = cashRegisterService.deleteCashSheetById(sheetId);

        // then
        verify(cashSheetRepo, times(1)).findCashSheetByIdAndDeletedIsFalse(eq(sheetId));
        verify(cashSheetRepo, times(1)).save(argumentCaptor.capture());

        CashSheet savedCashSheet = argumentCaptor.getValue();
        assertFalse(savedCashSheet.isProcessed());
        assertTrue(savedCashSheet.isDeleted());
        assertEquals("Success", string);
    }

    @Test
    void deleteCashSheetById_WhenSheetIsProcessed() {
        // given
        Long sheetId = 1L;
        cashSheet.setProcessed(true);

        // when
        when(cashSheetRepo.findCashSheetByIdAndDeletedIsFalse(eq(sheetId)))
                .thenReturn(Optional.of(cashSheet));

        // call the method
        String string = cashRegisterService.deleteCashSheetById(sheetId);

        // then
        verify(cashSheetRepo, times(1)).findCashSheetByIdAndDeletedIsFalse(eq(sheetId));
        verify(cashSheetRepo, never()).save(eq(cashSheet));

        assertEquals("Error", string);
    }

    @Test
    void saveCashSheet() {
        // call the method
        cashRegisterService.saveCashSheet(cashSheet);

        // then
        verify(cashSheetRepo).save(cashSheet);
    }

    @Test
    void updateCashSheetFromInvoice() {
        // given
        Invoice invoice = new Invoice();
        invoice.setId(1L);

        // when
        when(cashSheetRepo.findCashSheetByInvoice_Id(eq(invoice.getId())))
                .thenReturn(Optional.of(cashSheet));
        doNothing().when(cashSheetMapper).updateCashSheetFromInvoice(cashSheet, invoice);

        // call the method
        cashRegisterService.updateCashSheetFromInvoice(invoice);

        // then
        verify(cashSheetRepo, times(1)).findCashSheetByInvoice_Id(eq(invoice.getId()));
        verify(cashSheetMapper, times(1)).updateCashSheetFromInvoice(cashSheet, invoice);
    }

    @Test
    void updateCashSheetFromInvoice_WhenCashSheetNotFound() {
        // given
        Invoice invoice = new Invoice();
        invoice.setId(1L);

        // when
        when(cashSheetRepo.findCashSheetByInvoice_Id(eq(invoice.getId())))
                .thenReturn(Optional.empty());

        // call the method
        assertThrows(EntityNotFoundException.class, () -> cashRegisterService.updateCashSheetFromInvoice(invoice));

        // then
        verify(cashSheetRepo, times(1)).findCashSheetByInvoice_Id(eq(invoice.getId()));
    }
}