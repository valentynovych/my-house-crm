package com.example.myhouse24rest.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "owner_password_reset_token")
public class OwnerPasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String token;
    @Column(nullable = false)
    private Instant expirationDate;
    private static final int EXPIRATION = 20;
    @OneToOne
    @JoinColumn(nullable = false, name = "owner_id", referencedColumnName = "id")
    private ApartmentOwner apartmentOwner;

    public OwnerPasswordResetToken(String token, ApartmentOwner apartmentOwner) {
        this.token = token;
        this.apartmentOwner = apartmentOwner;
        setExpirationDate();
    }

    public OwnerPasswordResetToken() {
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

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate() {
        this.expirationDate = calculateExpirationDate();
    }
    private Instant calculateExpirationDate(){
        return Instant.now().plus(EXPIRATION, ChronoUnit.MINUTES);
    }
    public ApartmentOwner getApartmentOwner() {
        return apartmentOwner;
    }

    public void setApartmentOwner(ApartmentOwner apartmentOwner) {
        this.apartmentOwner = apartmentOwner;
    }
}
