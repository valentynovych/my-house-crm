package com.example.myhouse24admin.model.messages;

import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerShortResponse;

import java.time.Instant;
import java.util.List;

public class MessageTableResponse {
    private Long id;
    private List<ApartmentOwnerShortResponse> apartmentOwners;
    private String subject;
    private String text;
    private Instant sendDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ApartmentOwnerShortResponse> getApartmentOwners() {
        return apartmentOwners;
    }

    public void setApartmentOwners(List<ApartmentOwnerShortResponse> apartmentOwners) {
        this.apartmentOwners = apartmentOwners;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getSendDate() {
        return sendDate;
    }

    public void setSendDate(Instant sendDate) {
        this.sendDate = sendDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
