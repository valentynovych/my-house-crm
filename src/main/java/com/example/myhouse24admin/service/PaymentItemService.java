package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.paymentItem.PaymentItemDto;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface PaymentItemService {
    Page<PaymentItemDto> getPaymentItems(int page, int pageSize);

    PaymentItemDto getItemById(Long itemId);

    void addItem(PaymentItemDto paymentItemDto);

    void editItemById(Long itemId, PaymentItemDto paymentItem);

    boolean deleteItemById(Long itemId);

    Map<String, String> getItemTypes();
}
