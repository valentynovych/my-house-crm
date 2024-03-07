package com.example.myhouse24admin.mapper;


import com.example.myhouse24admin.entity.PaymentItem;
import com.example.myhouse24admin.model.paymentItem.PaymentItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentItemMapper {
    PaymentItemMapper MAPPER = Mappers.getMapper(PaymentItemMapper.class);

    PaymentItemDto paymentItemToPaymentItemDto(PaymentItem paymentItem);

    PaymentItem paymentItemDtoToPaymentItem(PaymentItemDto paymentItemDto);

    List<PaymentItemDto> paymentItemListToPaymentItemDtoList(List<PaymentItem> paymentItems);
}
