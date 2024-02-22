package com.example.myhouse24admin.model.meterReadings;

import com.example.myhouse24admin.entity.MeterReadingStatus;

import java.math.BigDecimal;

public record ApartmentMeterReadingResponse(
        Long id,
        String number,
        MeterReadingStatus status,
        String creationDate,
        String houseName,
        String sectionName,
        String apartmentNumber,
        String serviceName,
        BigDecimal readings,
        String measurementName
) {
}
