package com.example.myhouse24admin.model.services;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public class UnitOfMeasurementDto {
    private Long id;
    @NotEmpty(message = "{validation-not-empty}")
    @Size(min = 1, max = 10, message = "{validation-size-min-max}")
    private String name;

    public UnitOfMeasurementDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UnitOfMeasurementDto() {
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
}
