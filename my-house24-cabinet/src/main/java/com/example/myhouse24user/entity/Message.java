package com.example.myhouse24user.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "send_date", nullable = false)
    private Instant sendDate;
    @Column(length = 100, nullable = false)
    private String subject;
    @Column(length = 3000, nullable = false)
    private String text;
    @Column(nullable = false)
    private boolean deleted;
    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "id", nullable = false)
    private Staff staff;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
