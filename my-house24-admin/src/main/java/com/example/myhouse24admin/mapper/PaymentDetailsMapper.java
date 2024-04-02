package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.PaymentDetail;
import com.example.myhouse24admin.model.paymentDetails.PaymentDetailsDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentDetailsMapper {


    PaymentDetailsDto paymentDetailsToPaymentDetailsDto(PaymentDetail paymentDetail);

    PaymentDetail paymentDetailsDtoToPaymentDetails(PaymentDetailsDto paymentDetailsDto);
}
