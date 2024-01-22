package com.example.myhouse24admin.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;
    @Column(nullable = false)
    private boolean isProcessed;
    @Column(nullable = false)
    private boolean deleted;
    @ManyToOne
    @JoinColumn(name = "personal_account_id", referencedColumnName = "id", nullable = false)
    private PersonalAccount personalAccount;
    @ManyToMany
    @JoinTable(
            name = "invoices_readings",
            joinColumns = { @JoinColumn(name = "invoice_id") },
            inverseJoinColumns = { @JoinColumn(name = "reading_id") }
    )
    private List<MeterReading> meterReadings = new ArrayList<>();

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

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public PersonalAccount getPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(PersonalAccount personalAccount) {
        this.personalAccount = personalAccount;
    }

    public List<MeterReading> getMeterReadings() {
        return meterReadings;
    }

    public void setMeterReadings(List<MeterReading> meterReadings) {
        this.meterReadings = meterReadings;
    }
}
