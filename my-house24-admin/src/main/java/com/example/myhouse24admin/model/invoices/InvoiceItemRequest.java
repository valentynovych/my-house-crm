package com.example.myhouse24admin.model.invoices;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class InvoiceItemRequest {
    @NotNull(message = "{validation-not-empty}")
    private BigDecimal pricePerUnit;
    @NotNull(message = "{validation-not-empty}")
    private BigDecimal cost;
    @NotNull(message = "{validation-not-empty}")
    private BigDecimal amount;
    @NotNull(message = "{validation-not-empty}")
    private Long serviceId;

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}