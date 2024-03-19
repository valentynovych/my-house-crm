package com.example.myhouse24user.model.masterRequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class MasterRequestAddRequest {
    @NotEmpty(message = "{validation-field-required}")
    private String masterType;
    @NotNull(message = "{validation-field-required}")
    private Long apartmentId;
    @NotEmpty(message = "{validation-field-required}")
    @Size(min = 10, max = 500, message = "{validation-size-min-max}")
    private String description;
    @NotNull(message = "{validation-field-required}")
    private Instant visitDate;

    public String getMasterType() {
        return masterType;
    }

    public void setMasterType(String masterType) {
        this.masterType = masterType;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Instant visitDate) {
        this.visitDate = visitDate;
    }
}
