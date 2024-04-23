package com.example.myhouse24admin.model.invoices;

import com.example.myhouse24admin.entity.InvoiceStatus;

import java.math.BigDecimal;
import java.util.List;

public class ViewInvoiceResponse {
    private String number;
    private String creationDate;
    private boolean isProcessed;
    private InvoiceStatus invoiceStatus;
    private OwnerNameResponse ownerNameResponse;
    private AccountNumberResponse accountNumberResponse;
    private String phoneNumber;
    private HouseNameResponse houseNameResponse;
    private ApartmentNumberResponse apartmentNumberResponse;
    private String section;
    private TariffNameResponse tariffNameResponse;
    private List<InvoiceItemResponse> itemResponses;
    private BigDecimal totalPrice;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public List<InvoiceItemResponse> getItemResponses() {
        return itemResponses;
    }

    public void setItemResponses(List<InvoiceItemResponse> itemResponses) {
        this.itemResponses = itemResponses;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OwnerNameResponse getOwnerNameResponse() {
        return ownerNameResponse;
    }

    public void setOwnerNameResponse(OwnerNameResponse ownerNameResponse) {
        this.ownerNameResponse = ownerNameResponse;
    }

    public AccountNumberResponse getAccountNumberResponse() {
        return accountNumberResponse;
    }

    public void setAccountNumberResponse(AccountNumberResponse accountNumberResponse) {
        this.accountNumberResponse = accountNumberResponse;
    }

    public HouseNameResponse getHouseNameResponse() {
        return houseNameResponse;
    }

    public void setHouseNameResponse(HouseNameResponse houseNameResponse) {
        this.houseNameResponse = houseNameResponse;
    }

    public ApartmentNumberResponse getApartmentNumberResponse() {
        return apartmentNumberResponse;
    }

    public void setApartmentNumberResponse(ApartmentNumberResponse apartmentNumberResponse) {
        this.apartmentNumberResponse = apartmentNumberResponse;
    }

    public TariffNameResponse getTariffNameResponse() {
        return tariffNameResponse;
    }

    public void setTariffNameResponse(TariffNameResponse tariffNameResponse) {
        this.tariffNameResponse = tariffNameResponse;
    }
}
