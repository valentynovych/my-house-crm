package com.example.myhouse24admin.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "staff_password_reset_token")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String token;
    @Column(nullable = false)
    private Instant expirationDate;
    private static final int EXPIRATION = 20;
    @OneToOne
    @JoinColumn(nullable = false, name = "staff_id", referencedColumnName = "id")
    private Staff staff;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, Staff staff) {
        this.token = token;
        this.expirationDate = calculateExpirationDate();
        this.staff = staff;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public void setExpirationDate() {
        this.expirationDate = calculateExpirationDate();
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }
    private Instant calculateExpirationDate(){
        return Instant.now().plus(EXPIRATION, ChronoUnit.MINUTES);
    }
    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
