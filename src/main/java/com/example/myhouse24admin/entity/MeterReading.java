package com.example.myhouse24admin.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meter_readings")
public class MeterReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeterReadingStatus status;
    @Column(nullable = false)
    private BigDecimal readings;
    @Column(nullable = false)
    private boolean deleted;
    @ManyToOne
    @JoinColumn(name = "apartment_id", referencedColumnName = "id", nullable = false)
    private Apartment apartment;
    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    private Service service;
    @ManyToMany(mappedBy = "meterReadings")
    private List<Invoice> invoices = new ArrayList<>();

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

    public MeterReadingStatus getStatus() {
        return status;
    }

    public void setStatus(MeterReadingStatus status) {
        this.status = status;
    }

    public BigDecimal getReadings() {
        return readings;
    }

    public void setReadings(BigDecimal readings) {
        this.readings = readings;
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

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}
