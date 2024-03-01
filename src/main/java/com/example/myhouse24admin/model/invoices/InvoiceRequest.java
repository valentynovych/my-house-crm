package com.example.myhouse24admin.model.invoices;

import com.example.myhouse24admin.entity.InvoiceItem;
import com.example.myhouse24admin.entity.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InvoiceRequest {
    private String creationDate;
    @NotNull(message = "{validation-not-empty}")
    private Long apartmentId;
    @NotNull(message = "{validation-not-empty}")
    private Long house;
    @NotNull(message = "{validation-not-empty}")
    private InvoiceStatus status;
    @NotNull(message = "{validation-not-empty}")
    private BigDecimal paid;
    private boolean isProcessed;
    @Valid
    @NotNull(message = "{validation-necessary-service}")
    private List<InvoiceItemRequest> itemRequests;

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public Long getHouse() {
        return house;
    }

    public void setHouse(Long house) {
        this.house = house;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public BigDecimal getPaid() {
        return paid;
    }

    public void setPaid(BigDecimal paid) {
        this.paid = paid;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public List<InvoiceItemRequest> getItemRequests() {
        return itemRequests;
    }

    public void setItemRequests(List<InvoiceItemRequest> itemRequests) {
        this.itemRequests = itemRequests;
    }
}
