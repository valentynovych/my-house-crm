package com.example.myhouse24admin.model.apartmentOwner;

import com.example.myhouse24admin.entity.OwnerStatus;

import java.util.List;

public class TableApartmentOwnerResponse {
    private Long id;
    private String ownerId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String house;
    private String apartmentNumber;
    private String creationDate;
    private OwnerStatus status;
    private boolean hasDebt;
    private List<HouseApartmentResponse> houseApartmentResponses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public OwnerStatus getStatus() {
        return status;
    }

    public void setStatus(OwnerStatus status) {
        this.status = status;
    }

    public boolean isHasDebt() {
        return hasDebt;
    }

    public void setHasDebt(boolean hasDebt) {
        this.hasDebt = hasDebt;
    }

    public List<HouseApartmentResponse> getHouseApartmentResponses() {
        return houseApartmentResponses;
    }

    public void setHouseApartmentResponses(List<HouseApartmentResponse> houseApartmentResponses) {
        this.houseApartmentResponses = houseApartmentResponses;
    }
}
