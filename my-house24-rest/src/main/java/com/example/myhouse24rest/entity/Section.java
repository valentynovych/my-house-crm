package com.example.myhouse24rest.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(nullable = false)
    private boolean deleted;
    @ManyToOne
    @JoinColumn(name = "house_id", referencedColumnName = "id", nullable = false)
    private House house;
    @Column(length = 8, nullable = false)
    private String rangeApartmentNumbers;
    @OneToMany(mappedBy = "section")
    private List<Apartment> apartments = new ArrayList<>();

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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public String getRangeApartmentNumbers() {
        return rangeApartmentNumbers;
    }

    public void setRangeApartmentNumbers(String rangeApartmentNumbers) {
        this.rangeApartmentNumbers = rangeApartmentNumbers;
    }

    public List<Apartment> getApartments() {
        return apartments;
    }

    public void setApartments(List<Apartment> apartments) {
        this.apartments = apartments;
    }
}
