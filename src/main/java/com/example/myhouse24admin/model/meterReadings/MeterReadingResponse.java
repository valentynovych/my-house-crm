package com.example.myhouse24admin.model.meterReadings;

import com.example.myhouse24admin.entity.MeterReadingStatus;

import java.math.BigDecimal;

public record MeterReadingResponse(
        String number,
        String creationDate,
        BigDecimal readings,
        MeterReadingStatus status,
        HouseNameResponse houseNameResponse,
        SectionNameResponse sectionNameResponse,
        ApartmentNumberResponse apartmentNumberResponse,
        ServiceNameResponse serviceNameResponse
) {
}
