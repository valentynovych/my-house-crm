package com.example.myhouse24admin.model.services;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ServiceDto {
    private Long id;
    @Size(min = 3, max = 100, message = "{validation-size-min-max}")
    @NotEmpty(message = "{validation-not-empty}")
    private String name;
    private boolean showInMeter;
    @NotNull(message = "{validation-not-empty}")
    private Long unitOfMeasurementId;

    public ServiceDto() {
    }

    public ServiceDto(Long id, String name, boolean showInMeter, Long unitOfMeasurementId) {
        this.id = id;
        this.name = name;
        this.showInMeter = showInMeter;
        this.unitOfMeasurementId = unitOfMeasurementId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowInMeter() {
        return showInMeter;
    }

    public void setShowInMeter(boolean showInMeter) {
        this.showInMeter = showInMeter;
    }

    public Long getUnitOfMeasurementId() {
        return unitOfMeasurementId;
    }

    public void setUnitOfMeasurementId(Long unitOfMeasurementId) {
        this.unitOfMeasurementId = unitOfMeasurementId;
    }
}
