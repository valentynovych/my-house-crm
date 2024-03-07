package com.example.myhouse24admin.model.paymentItem;

import com.example.myhouse24admin.entity.PaymentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PaymentItemDto {

    private Long id;
    @NotEmpty(message = "{validation-not-empty}")
    @Size(min = 2, max = 50, message = "{validation-size-min-max}")
    private String name;
    private boolean deleted;
    @NotNull(message = "{validation-not-empty}")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
}
