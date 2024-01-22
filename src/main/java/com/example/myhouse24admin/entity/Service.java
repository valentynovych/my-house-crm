package com.example.myhouse24admin.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(name = "show_in_meter", nullable = false)
    private boolean showInMeter;
    @Column(nullable = false)
    private boolean deleted;
    @ManyToOne
    @JoinColumn(name = "measurement_id", referencedColumnName = "id", nullable = false)
    private UnitOfMeasurement unitOfMeasurement;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowInMeter() {
        return showInMeter;
    }

    public void setShowInMeter(boolean showInMeter) {
        this.showInMeter = showInMeter;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public UnitOfMeasurement getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }
}
