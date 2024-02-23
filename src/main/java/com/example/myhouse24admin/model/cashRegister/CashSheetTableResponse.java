package com.example.myhouse24admin.model.cashRegister;

import com.example.myhouse24admin.entity.CashSheetType;
import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerShortResponse;
import com.example.myhouse24admin.model.paymentItem.PaymentItemDto;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountShortResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.Instant;

public class CashSheetTableResponse {

    private Long id;
    private String sheetNumber;
    @Enumerated(EnumType.STRING)
    private CashSheetType sheetType;
    private Instant creationDate;
    private boolean isProcessed;
    private BigDecimal amount;
    private String comment;
    private boolean deleted;
    private PersonalAccountShortResponse personalAccount;
    private ApartmentOwnerShortResponse apartmentOwner;
    private PaymentItemDto paymentItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CashSheetType getSheetType() {
        return sheetType;
    }

    public void setSheetType(CashSheetType sheetType) {
        this.sheetType = sheetType;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public PersonalAccountShortResponse getPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(PersonalAccountShortResponse personalAccount) {
        this.personalAccount = personalAccount;
    }

    public ApartmentOwnerShortResponse getApartmentOwner() {
        return apartmentOwner;
    }

    public void setApartmentOwner(ApartmentOwnerShortResponse apartmentOwner) {
        this.apartmentOwner = apartmentOwner;
    }

    public PaymentItemDto getPaymentItem() {
        return paymentItem;
    }

    public void setPaymentItem(PaymentItemDto paymentItem) {
        this.paymentItem = paymentItem;
    }

    public String getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }
}
