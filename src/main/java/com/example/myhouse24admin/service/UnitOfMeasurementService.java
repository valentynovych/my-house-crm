package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.services.UnitOfMeasurementDto;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDtoListWrap;

import java.util.List;

public interface UnitOfMeasurementService {
    List<UnitOfMeasurementDto> getAllMeasurementUnits();

    void updateMeasurementUnist(UnitOfMeasurementDtoListWrap measurementListWrap);
}
