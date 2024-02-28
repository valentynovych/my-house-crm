package com.example.myhouse24admin.model.invoices;

import java.math.BigDecimal;

public record TariffItemResponse(
        Long serviceId,
        String serviceName,
        String unitOfMeasurement,
        BigDecimal servicePrice
) {
}
