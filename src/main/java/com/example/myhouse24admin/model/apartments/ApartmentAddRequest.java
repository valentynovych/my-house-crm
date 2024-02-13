package com.example.myhouse24admin.model.apartments;

import jakarta.validation.constraints.NotNull;

public class ApartmentAddRequest {
    @NotNull(message = "{validation-field-required}")
    private Long houseId;
    private Long sectionId;
    private Long floorId;
    private Long ownerId;
    private Long tariffId;
    private Integer apartmentNumber;
    private Double area;
    private Long personalAccountId;

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getTariffId() {
        return tariffId;
    }

    public void setTariffId(Long tariffId) {
        this.tariffId = tariffId;
    }

    public Integer getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(Integer apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Long getPersonalAccountId() {
        return personalAccountId;
    }

    public void setPersonalAccountId(Long personalAccountId) {
        this.personalAccountId = personalAccountId;
    }
}
