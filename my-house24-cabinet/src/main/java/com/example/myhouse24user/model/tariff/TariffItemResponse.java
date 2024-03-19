package com.example.myhouse24user.model.tariff;

import java.math.BigDecimal;

public record TariffItemResponse(Long id, String serviceName, String unitOfMeasurementName, BigDecimal servicePrice) {
}
