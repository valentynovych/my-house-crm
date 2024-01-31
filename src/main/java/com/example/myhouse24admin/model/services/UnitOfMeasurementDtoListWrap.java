package com.example.myhouse24admin.model.services;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

public class UnitOfMeasurementDtoListWrap {
    @Valid
    private List<UnitOfMeasurementDto> unitOfMeasurements;
    private List<Long> unitsToDelete = new ArrayList<>();

    public List<UnitOfMeasurementDto> getUnitOfMeasurements() {
        return unitOfMeasurements;
    }

    public void setUnitOfMeasurements(List<UnitOfMeasurementDto> unitOfMeasurements) {
        this.unitOfMeasurements = unitOfMeasurements;
    }

    public List<Long> getUnitsToDelete() {
        return unitsToDelete;
    }

    public void setUnitsToDelete(List<Long> unitsToDelete) {
        this.unitsToDelete = unitsToDelete;
    }
}
