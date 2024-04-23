package com.example.myhouse24admin.model.personalAccounts;

import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class PersonalAccountTableResponse {
    private Long id;
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private PersonalAccountStatus status;
    private ApartmentResponse apartment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public ApartmentResponse getApartment() {
        return apartment;
    }

    public void setApartment(ApartmentResponse apartment) {
        this.apartment = apartment;
    }
}
