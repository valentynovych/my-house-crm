package com.example.myhouse24admin.model.messages;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class MessageSendRequest {
    @NotEmpty(message = "{validation-field-required}")
    @Size(max = 100, message = "{validation-size-max}")
    private String subject;
    @NotEmpty(message = "{validation-field-required}")
    @Size(max = 3000, message = "{validation-size-max}")
    private String text;
    @Max(value = 1500L, message = "{validation-size-max-value}")
    @Min(value = 2, message = "{validation-field-required}")
    private Long textLength;
    private boolean forArrears;
    private Long house;
    private Long section;
    private Long floor;
    private Long apartment;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTextLength() {
        return textLength;
    }

    public void setTextLength(Long textLength) {
        this.textLength = textLength;
    }

    public boolean isForArrears() {
        return forArrears;
    }

    public void setForArrears(boolean forArrears) {
        this.forArrears = forArrears;
    }

    public Long getHouse() {
        return house;
    }

    public void setHouse(Long house) {
        this.house = house;
    }

    public Long getSection() {
        return section;
    }

    public void setSection(Long section) {
        this.section = section;
    }

    public Long getFloor() {
        return floor;
    }

    public void setFloor(Long floor) {
        this.floor = floor;
    }

    public Long getApartment() {
        return apartment;
    }

    public void setApartment(Long apartment) {
        this.apartment = apartment;
    }
}
