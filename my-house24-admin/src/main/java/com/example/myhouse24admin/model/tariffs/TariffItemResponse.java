package com.example.myhouse24admin.model.tariffs;

import com.example.myhouse24admin.model.services.ServiceResponse;

import java.math.BigDecimal;

public record TariffItemResponse(
        Long id,
        BigDecimal servicePrice,
        String currency,
        ServiceResponse service) {
}
