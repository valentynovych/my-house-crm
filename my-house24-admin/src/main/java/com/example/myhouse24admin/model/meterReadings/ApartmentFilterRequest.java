package com.example.myhouse24admin.model.meterReadings;

import com.example.myhouse24admin.entity.MeterReadingStatus;

public record ApartmentFilterRequest(
        String number,
        MeterReadingStatus status,
        String creationDate,
        Long houseId,
        Long sectionId,
        String apartment,
        Long serviceId
) {
}
