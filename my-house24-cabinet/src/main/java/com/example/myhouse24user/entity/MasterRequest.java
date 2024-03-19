package com.example.myhouse24user.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "master_requests")
public class MasterRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Instant creationDate;
    @Column(name = "visit_date", nullable = false)
    private Instant visitDate;
    @Column(nullable = false)
    private boolean deleted;
    @Column(name = "apartment_owner_phone", length = 13, nullable = false)
    private String apartmentOwnerPhone;
    @Enumerated(EnumType.STRING)
    private MasterRequestStatus status;
    @ManyToOne
    @JoinColumn(name = "apartment_id", referencedColumnName = "id", nullable = false)
    private Apartment apartment;
    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "id")
    private Staff staff;
    @Column(name = "master_type", length = 50, nullable = false)
    private String masterType;
    @Column(length = 500, nullable = false)
    private String description;
    @Column(length = 1000)
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Instant visitDate) {
        this.visitDate = visitDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getApartmentOwnerPhone() {
        return apartmentOwnerPhone;
    }

    public void setApartmentOwnerPhone(String apartmentOwnerPhone) {
        this.apartmentOwnerPhone = apartmentOwnerPhone;
    }

    public MasterRequestStatus getStatus() {
        return status;
    }

    public void setStatus(MasterRequestStatus status) {
        this.status = status;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMasterType() {
        return masterType;
    }

    public void setMasterType(String masterType) {
        this.masterType = masterType;
    }
}
