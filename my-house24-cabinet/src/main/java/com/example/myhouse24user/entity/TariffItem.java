package com.example.myhouse24user.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tariff_items")
public class TariffItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "service_price", nullable = false)
    private BigDecimal servicePrice;
    @Column(length = 10, nullable = false)
    private String currency;
    @ManyToOne
    @JoinColumn(name = "tariff_id", referencedColumnName = "id", nullable = false)
    private Tariff tariff;
    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    private Service service;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(BigDecimal servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
