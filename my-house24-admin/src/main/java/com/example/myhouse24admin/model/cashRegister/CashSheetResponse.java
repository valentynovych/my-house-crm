package com.example.myhouse24admin.model.cashRegister;

import com.example.myhouse24admin.entity.CashSheetType;
import com.example.myhouse24admin.model.paymentItem.PaymentItemDto;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountWithApartmentOwnerResponse;
import com.example.myhouse24admin.model.staff.StaffShortResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.Instant;

public class CashSheetResponse {
    private Long id;
    private String sheetNumber;
    private Instant creationDate;
    private boolean isProcessed;
    @Enumerated(EnumType.STRING)
    private CashSheetType sheetType;
    private PersonalAccountWithApartmentOwnerResponse personalAccount;
    private PaymentItemDto paymentItem;
    private BigDecimal amount;
    private StaffShortResponse staff;
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public CashSheetType getSheetType() {
        return sheetType;
    }

    public void setSheetType(CashSheetType sheetType) {
        this.sheetType = sheetType;
    }

    public PersonalAccountWithApartmentOwnerResponse getPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(PersonalAccountWithApartmentOwnerResponse personalAccount) {
        this.personalAccount = personalAccount;
    }

    public PaymentItemDto getPaymentItem() {
        return paymentItem;
    }

    public void setPaymentItem(PaymentItemDto paymentItem) {
        this.paymentItem = paymentItem;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public StaffShortResponse getStaff() {
        return staff;
    }

    public void setStaff(StaffShortResponse staff) {
        this.staff = staff;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }
}
