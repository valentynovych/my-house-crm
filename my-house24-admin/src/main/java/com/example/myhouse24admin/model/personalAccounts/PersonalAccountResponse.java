package com.example.myhouse24admin.model.personalAccounts;

import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;

public class PersonalAccountResponse {
    private Long id;
    private Long accountNumber;
    private PersonalAccountStatus status;
    private ApartmentResponse apartment;

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

    public ApartmentResponse getApartment() {
        return apartment;
    }

    public void setApartment(ApartmentResponse apartment) {
        this.apartment = apartment;
    }
}