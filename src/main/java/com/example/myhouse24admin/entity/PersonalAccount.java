package com.example.myhouse24admin.entity;

import jakarta.persistence.*;

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
    @OneToOne
    @JoinColumn(name = "apartment_id", referencedColumnName = "id", nullable = false)
    private Apartment apartment;

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
}
