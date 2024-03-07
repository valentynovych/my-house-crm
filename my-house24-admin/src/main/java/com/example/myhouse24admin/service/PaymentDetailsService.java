package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.paymentDetails.PaymentDetailsDto;

public interface PaymentDetailsService {

    PaymentDetailsDto getPaymentDetails();

    void updatePaymentDetails(PaymentDetailsDto paymentDetailsDto);
}
