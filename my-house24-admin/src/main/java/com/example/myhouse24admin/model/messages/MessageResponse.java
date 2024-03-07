package com.example.myhouse24admin.model.messages;

import com.example.myhouse24admin.model.staff.StaffShortResponse;

import java.time.Instant;

public class MessageResponse {
    private Long id;
    private String subject;
    private String text;
    private StaffShortResponse staff;
    private Instant sendDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public StaffShortResponse getStaff() {
        return staff;
    }

    public void setStaff(StaffShortResponse staff) {
        this.staff = staff;
    }

    public Instant getSendDate() {
        return sendDate;
    }

    public void setSendDate(Instant sendDate) {
        this.sendDate = sendDate;
    }
}
