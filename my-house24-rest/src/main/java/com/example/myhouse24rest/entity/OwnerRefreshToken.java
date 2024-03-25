package com.example.myhouse24rest.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "owner_refresh_token")
public class OwnerRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private ApartmentOwner owner;
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;
    @Column(name = "expired_date", nullable = false)
    private Instant expiredDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApartmentOwner getOwner() {
        return owner;
    }

    public void setOwner(ApartmentOwner owner) {
        this.owner = owner;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Instant getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Instant expiredDate) {
        this.expiredDate = expiredDate;
    }
}
