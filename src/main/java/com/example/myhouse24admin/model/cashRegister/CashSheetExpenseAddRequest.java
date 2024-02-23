package com.example.myhouse24admin.model.cashRegister;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CashSheetExpenseAddRequest {
    @NotEmpty(message = "{validation-field-required}")
    private String sheetNumber;
    @NotEmpty(message = "{validation-field-required}")
    private String creationDate;
    @NotNull(message = "{validation-field-required}")
    @Min(value = 1, message = "{validation-invalid-value}")
    private Long paymentItemId;
    @NotNull(message = "{validation-field-required}")
    @DecimalMax(value = "50000", message = "{validation-amount-max-value}")
    @DecimalMin(value = "1", message = "{validation-amount-min-value}")
    private BigDecimal amount;
    @NotNull(message = "{validation-field-required}")
    private Boolean isProcessed;
    @NotNull(message = "{validation-field-required}")
    private Long staffId;
    @Size(max = 1000, message = "{validation-size-max}")
    private String comment;

    public String getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Long getPaymentItemId() {
        return paymentItemId;
    }

    public void setPaymentItemId(Long paymentItemId) {
        this.paymentItemId = paymentItemId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
