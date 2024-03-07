package com.example.myhouse24admin.model.services;

public record ServiceResponse(
        Long id,
        String name,
        boolean showInMeter,
        UnitOfMeasurementDto unitOfMeasurement) {
}
