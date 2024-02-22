package com.example.myhouse24admin.model.meterReadings;

import java.math.BigDecimal;

public record TableMeterReadingResponse(
        Long id,
        Long apartmentId,
        String houseName,
        String sectionName,
        String apartmentName,
        String serviceName,
        BigDecimal readings,
        String measurementName
) {
}
