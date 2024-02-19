package com.example.myhouse24admin.model.personalAccounts;

import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.validators.personalAccountValidation.ValidatePersonalAccountUpdateRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@ValidatePersonalAccountUpdateRequest
public class PersonalAccountUpdateRequest {
    @NotNull(message = "{validation-field-required}")
    private Long id;
    @NotNull(message = "{validation-field-required}")
    @Min(value = 1, message = "{validation-value-isPositive}")
    @Max(value = 9_999_999_999L, message = "{validation-size-max}")
    private Long accountNumber;
    @NotNull(message = "{validation-field-required}")
    private PersonalAccountStatus status;
    private Long houseId;
    private Long sectionId;
    private Long apartmentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public PersonalAccountStatus getStatus() {
        return status;
    }

    public void setStatus(PersonalAccountStatus status) {
        this.status = status;
    }

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

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }
}
