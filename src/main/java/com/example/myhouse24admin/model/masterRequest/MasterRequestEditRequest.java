package com.example.myhouse24admin.model.masterRequest;

import com.example.myhouse24admin.entity.MasterRequestStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class MasterRequestEditRequest {

    @NotNull(message = "{validation-field-required}")
    private Long id;
    @NotNull(message = "{validation-field-required}")
    private Instant visitDate;
    @NotNull(message = "{validation-field-required}")
    private Long apartmentOwnerId;
    @NotEmpty(message = "{validation-field-required}")
    private String apartmentOwnerPhone;
    @Size(max = 200, message = "{validation-size-max}")
    @NotEmpty(message = "{validation-field-required}")
    private String description;
    @NotNull(message = "{validation-field-required}")
    private Long apartmentId;
    @NotEmpty(message = "{validation-field-required}")
    private String masterType;
    @Enumerated(EnumType.STRING)
    private MasterRequestStatus status;
    @NotNull(message = "{validation-field-required}")
    private Long masterId;
    @Size(max = 1000, message = "{validation-size-max}")
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Instant visitDate) {
        this.visitDate = visitDate;
    }

    public Long getApartmentOwnerId() {
        return apartmentOwnerId;
    }

    public void setApartmentOwnerId(Long apartmentOwnerId) {
        this.apartmentOwnerId = apartmentOwnerId;
    }

    public String getApartmentOwnerPhone() {
        return apartmentOwnerPhone;
    }

    public void setApartmentOwnerPhone(String apartmentOwnerPhone) {
        this.apartmentOwnerPhone = apartmentOwnerPhone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getMasterType() {
        return masterType;
    }

    public void setMasterType(String masterType) {
        this.masterType = masterType;
    }

    public MasterRequestStatus getStatus() {
        return status;
    }

    public void setStatus(MasterRequestStatus status) {
        this.status = status;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}