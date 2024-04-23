package com.example.myhouse24admin.model.personalAccounts;

import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.validators.personalAccountValidation.NotRequiredApartmentInPersonalAccount;
import com.example.myhouse24admin.validators.personalAccountValidation.UniquePersonalAccountNumber;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@NotRequiredApartmentInPersonalAccount
public class PersonalAccountAddRequest {

    @NotNull(message = "{validation-field-required}")
    @Pattern(regexp = "^[0-9]{5}-[0-9]{5}$" , message = "{validation-invalid-value}")
    @UniquePersonalAccountNumber
    private String accountNumber;
    @NotNull(message = "{validation-field-required}")
    private PersonalAccountStatus status;
    private Long houseId;
    private Long sectionId;
    private Long apartmentId;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
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
