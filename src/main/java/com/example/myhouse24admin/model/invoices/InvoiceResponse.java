package com.example.myhouse24admin.model.invoices;

import com.example.myhouse24admin.entity.InvoiceStatus;
import com.example.myhouse24admin.model.meterReadings.ApartmentNumberResponse;
import com.example.myhouse24admin.model.meterReadings.HouseNameResponse;
import com.example.myhouse24admin.model.meterReadings.SectionNameResponse;

import java.math.BigDecimal;
import java.util.List;

public class InvoiceResponse {
    private String number;
    private String creationDate;
    private SectionNameResponse sectionNameResponse;
    private HouseNameResponse houseNameResponse;
    private ApartmentNumberResponse apartmentNumberResponse;
    private InvoiceStatus status;
    private BigDecimal paid;
    private BigDecimal totalPrice;
    private boolean isProcessed;
    private OwnerResponse ownerResponse;
    private List<InvoiceItemResponse> itemResponses;

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

    public SectionNameResponse getSectionNameResponse() {
        return sectionNameResponse;
    }

    public void setSectionNameResponse(SectionNameResponse sectionNameResponse) {
        this.sectionNameResponse = sectionNameResponse;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public OwnerResponse getOwnerResponse() {
        return ownerResponse;
    }

    public void setOwnerResponse(OwnerResponse ownerResponse) {
        this.ownerResponse = ownerResponse;
    }

    public List<InvoiceItemResponse> getItemResponses() {
        return itemResponses;
    }

    public void setItemResponses(List<InvoiceItemResponse> itemResponses) {
        this.itemResponses = itemResponses;
    }
}
