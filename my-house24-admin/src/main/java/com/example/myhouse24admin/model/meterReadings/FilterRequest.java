package com.example.myhouse24admin.model.meterReadings;

public record FilterRequest(
        Long houseId,
        Long sectionId,
        String apartment,
        Long serviceId

) {
}
