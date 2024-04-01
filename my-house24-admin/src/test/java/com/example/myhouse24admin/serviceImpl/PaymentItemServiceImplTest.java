package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.PaymentItem;
import com.example.myhouse24admin.entity.PaymentType;
import com.example.myhouse24admin.mapper.PaymentItemMapper;
import com.example.myhouse24admin.model.paymentItem.PaymentItemDto;
import com.example.myhouse24admin.repository.CashSheetRepo;
import com.example.myhouse24admin.repository.PaymentItemRepo;
import com.example.myhouse24admin.specification.PaymentItemSpecification;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentItemServiceImplTest {

    @Mock
    private PaymentItemRepo paymentItemRepo;
    @Mock
    private CashSheetRepo cashSheetRepo;
    @Mock
    private PaymentItemMapper mapper;
    @InjectMocks
    private PaymentItemServiceImpl paymentItemService;
    private static List<PaymentItem> paymentItems;

    @BeforeEach
    void setUp() {
        paymentItems = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PaymentItem paymentItem = new PaymentItem();
            paymentItem.setId((long) i + 1);
            paymentItem.setPaymentType(PaymentType.INCOME);
            paymentItem.setDeleted(false);
            paymentItem.setName("paymentItem_" + i);
            paymentItems.add(paymentItem);
        }
    }

    @Test
    void getPaymentItems() {
        // given
        List<PaymentItemDto> paymentItemDtos = new ArrayList<>();
        for (PaymentItem paymentItem : paymentItems) {
            PaymentItemDto paymentItemDto = new PaymentItemDto();
            paymentItemDto.setId(paymentItem.getId());
            paymentItemDto.setPaymentType(paymentItem.getPaymentType());
            paymentItemDto.setName(paymentItem.getName());
            paymentItemDtos.add(paymentItemDto);
        }
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        when(paymentItemRepo.findAll(any(PaymentItemSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(paymentItems, pageable, 5));
        when(mapper.paymentItemListToPaymentItemDtoList(anyList()))
                .thenReturn(paymentItemDtos);

        // call the method
        Page<PaymentItemDto> result = paymentItemService.getPaymentItems(0, 10, new HashMap<>());

        // then
        assertFalse(result.getContent().isEmpty());
        assertEquals(5, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(paymentItemDtos, result.getContent());

        verify(paymentItemRepo, times(1)).findAll(any(PaymentItemSpecification.class), any(Pageable.class));
        verify(mapper, times(1)).paymentItemListToPaymentItemDtoList(anyList());
    }

    @Test
    void getItemById() {
        // given
        PaymentItem paymentItem = paymentItems.get(0);
        PaymentItemDto paymentItemDto = new PaymentItemDto();
        paymentItemDto.setId(paymentItem.getId());
        paymentItemDto.setPaymentType(paymentItem.getPaymentType());
        paymentItemDto.setName(paymentItem.getName());

        // when
        when(paymentItemRepo.findByIdAndDeletedIsFalse(1L))
                .thenReturn(Optional.of(paymentItem));
        when(mapper.paymentItemToPaymentItemDto(any(PaymentItem.class)))
                .thenReturn(paymentItemDto);

        // call the method
        PaymentItemDto result = paymentItemService.getItemById(1L);

        // then
        assertNotNull(result);
        assertEquals(paymentItemDto, result);

        verify(paymentItemRepo, times(1)).findByIdAndDeletedIsFalse(1L);
        verify(mapper, times(1)).paymentItemToPaymentItemDto(any(PaymentItem.class));
    }

    @Test
    void getItemById_WhenPaymentItemNotFound() {
        // when
        when(paymentItemRepo.findByIdAndDeletedIsFalse(eq(1L)))
                .thenReturn(Optional.empty());

        // call the method
        assertThrows(EntityNotFoundException.class, () -> paymentItemService.getItemById(1L));
        verify(paymentItemRepo, times(1)).findByIdAndDeletedIsFalse(1L);
    }

    @Test
    void addItem() {
        // given
        PaymentItem paymentItem = paymentItems.get(0);
        PaymentItemDto paymentItemDto = new PaymentItemDto();
        paymentItemDto.setId(paymentItem.getId());
        paymentItemDto.setPaymentType(paymentItem.getPaymentType());
        paymentItemDto.setName(paymentItem.getName());

        // when
        when(mapper.paymentItemDtoToPaymentItem(any(PaymentItemDto.class)))
                .thenReturn(paymentItem);
        when(paymentItemRepo.save(any(PaymentItem.class)))
                .thenReturn(paymentItem);

        // call the method
        paymentItemService.addItem(paymentItemDto);

        // then
        verify(mapper, times(1)).paymentItemDtoToPaymentItem(any(PaymentItemDto.class));
        verify(paymentItemRepo, times(1)).save(any(PaymentItem.class));
    }

    @Test
    void editItemById() {
        // given
        PaymentItem paymentItem = paymentItems.get(0);
        PaymentItemDto paymentItemDto = new PaymentItemDto();
        paymentItemDto.setId(paymentItem.getId());
        paymentItemDto.setPaymentType(paymentItem.getPaymentType());
        paymentItemDto.setName(paymentItem.getName());

        // when
        when(paymentItemRepo.existsById(1L))
                .thenReturn(true);
        when(mapper.paymentItemDtoToPaymentItem(any(PaymentItemDto.class)))
                .thenReturn(paymentItem);

        // call the method
        paymentItemService.editItemById(1L, paymentItemDto);

        // then
        verify(paymentItemRepo, times(1)).existsById(1L);
        verify(mapper, times(1)).paymentItemDtoToPaymentItem(any(PaymentItemDto.class));
        verify(paymentItemRepo, times(1)).save(any(PaymentItem.class));
    }

    @Test
    void editItemById_WhenPaymentItemNotFound() {
        // given
        PaymentItem paymentItem = paymentItems.get(0);
        PaymentItemDto paymentItemDto = new PaymentItemDto();
        paymentItemDto.setId(paymentItem.getId());
        paymentItemDto.setPaymentType(paymentItem.getPaymentType());
        paymentItemDto.setName(paymentItem.getName());

        // when
        when(paymentItemRepo.existsById(1L))
                .thenReturn(false);

        // call the method
        assertThrows(EntityNotFoundException.class, () -> paymentItemService.editItemById(1L, paymentItemDto));

        // then
        verify(paymentItemRepo, times(1)).existsById(1L);
    }

    @Test
    void deleteItemById() {
        // given
        PaymentItem paymentItem = paymentItems.get(0);
        ArgumentCaptor<PaymentItem> argumentCaptor = ArgumentCaptor.forClass(PaymentItem.class);

        // when
        when(paymentItemRepo.findByIdAndDeletedIsFalse(1L))
                .thenReturn(Optional.of(paymentItem));
        when(cashSheetRepo.existsCashSheetByPaymentItem_Id(1L))
                .thenReturn(false);

        // call the method
        boolean deleted = paymentItemService.deleteItemById(1L);

        // then
        assertTrue(deleted);
        verify(paymentItemRepo, times(1)).findByIdAndDeletedIsFalse(1L);
        verify(cashSheetRepo, times(1)).existsCashSheetByPaymentItem_Id(1L);
        verify(paymentItemRepo, times(1)).save(argumentCaptor.capture());

        PaymentItem savedPaymentItem = argumentCaptor.getValue();
        assertTrue(savedPaymentItem.isDeleted());
    }

    @Test
    void deleteItemById_WhenPaymentItemIsUsedInCashSheet() {
        // given
        PaymentItem paymentItem = paymentItems.get(0);

        // when
        when(paymentItemRepo.findByIdAndDeletedIsFalse(1L))
                .thenReturn(Optional.of(paymentItem));
        when(cashSheetRepo.existsCashSheetByPaymentItem_Id(1L))
                .thenReturn(true);

        // call the method
        boolean deleted = paymentItemService.deleteItemById(1L);

        // then
        assertFalse(deleted);
        verify(paymentItemRepo, times(1)).findByIdAndDeletedIsFalse(1L);
        verify(cashSheetRepo, times(1)).existsCashSheetByPaymentItem_Id(1L);
    }

    @Test
    void getItemTypes() {
        // call the method
        Map<String, String> paymentTypes = paymentItemService.getItemTypes();

        // then
        assertFalse(paymentTypes.isEmpty());
        assertEquals(2, paymentTypes.size());
    }

    @Test
    void getDefaultPaymentItemForInvoices_WhenPaymentItemIsExists() {
        // given
        PaymentItem paymentItem = paymentItems.get(0);

        // when
        when(paymentItemRepo.findByIdAndDeletedIsFalse(1L))
                .thenReturn(Optional.of(paymentItem));

        // call the method
        PaymentItem paymentItemResult = paymentItemService.getDefaultPaymentItemForInvoices();

        // then
        assertNotNull(paymentItemResult);
        assertEquals(paymentItem.getId(), paymentItemResult.getId());

        verify(paymentItemRepo, times(1)).findByIdAndDeletedIsFalse(1L);
    }

    @Test
    void getDefaultPaymentItemForInvoices_WhenPaymentItemIsNotFound() {
        // given
        PaymentItem paymentItem = paymentItems.get(0);

        // when
        when(paymentItemRepo.findByIdAndDeletedIsFalse(1L))
                .thenReturn(Optional.empty());
        when(paymentItemRepo.save(any(PaymentItem.class)))
                .thenReturn(paymentItem);

        // call the method
        PaymentItem paymentItemResult = paymentItemService.getDefaultPaymentItemForInvoices();

        // then
        assertNotNull(paymentItemResult);
        assertEquals(paymentItem.getId(), paymentItemResult.getId());

        verify(paymentItemRepo, times(1)).findByIdAndDeletedIsFalse(1L);
        verify(paymentItemRepo, times(1)).save(any(PaymentItem.class));
    }
}