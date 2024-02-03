package com.example.myhouse24admin.model.tariffs;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TariffItemRequest {
    private Long id;
    @DecimalMin(value = "0.01", message = "{validation-price-min}")
    @DecimalMax(value = "1000", message = "{validation-price-max}")
    @NotNull(message = "{validation-not-empty}")
    private BigDecimal servicePrice;
    @NotNull(message = "{validation-not-empty}")
    private Long serviceId;

    public TariffItemRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(BigDecimal servicePrice) {
        this.servicePrice = servicePrice;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
