package com.example.myhouse24admin.model.apartments;

import com.example.myhouse24admin.validators.apartmentValidation.RequiredNewOrPresentAccount;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
@RequiredNewOrPresentAccount
public class ApartmentAddRequest {
    @NotNull(message = "{validation-field-required}")
    private Long houseId;
    @NotNull(message = "{validation-field-required}")
    private Long sectionId;
    @NotNull(message = "{validation-field-required}")
    private Long floorId;
    @NotNull(message = "{validation-field-required}")
    private Long ownerId;
    @NotNull(message = "{validation-field-required}")
    private Long tariffId;
    @NotNull(message = "{validation-field-required}")
    @Min(value = 1, message = "{validation-field-required}")
    private Integer apartmentNumber;
    @NotNull(message = "{validation-field-required}")
    private Double area;
    private Long personalAccountId;
    private Long personalAccountNew;

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

    public Long getPersonalAccountNew() {
        return personalAccountNew;
    }

    public void setPersonalAccountNew(Long personalAccountNew) {
        this.personalAccountNew = personalAccountNew;
    }
}
