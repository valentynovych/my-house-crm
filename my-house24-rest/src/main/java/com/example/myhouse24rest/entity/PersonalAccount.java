package com.example.myhouse24rest.entity;

import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "personal_accounts")
public class PersonalAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonalAccountStatus status;
    @Column(nullable = false)
    private boolean deleted;
    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "apartment_id", referencedColumnName = "id")
    private Apartment apartment;
    @Column(nullable = false, unique = true)
    private String accountNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonalAccountStatus getStatus() {
        return status;
    }

    public void setStatus(PersonalAccountStatus status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public void setAccountNumber(String personalAccountNew) {
        this.accountNumber = personalAccountNew;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
