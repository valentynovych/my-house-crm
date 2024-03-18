package com.example.myhouse24user.model.owner;

import jakarta.persistence.Column;

import java.util.List;

public class ViewOwnerResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private String viberNumber;
    private String telegramUsername;
    private String email;
    private String avatar;
    List<ApartmentResponse> apartmentResponses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getViberNumber() {
        return viberNumber;
    }

    public void setViberNumber(String viberNumber) {
        this.viberNumber = viberNumber;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public void setTelegramUsername(String telegramUsername) {
        this.telegramUsername = telegramUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<ApartmentResponse> getApartmentResponses() {
        return apartmentResponses;
    }

    public void setApartmentResponses(List<ApartmentResponse> apartmentResponses) {
        this.apartmentResponses = apartmentResponses;
    }
}
