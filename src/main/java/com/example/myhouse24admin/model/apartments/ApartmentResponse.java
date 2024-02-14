package com.example.myhouse24admin.model.apartments;

import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerShortResponse;
import com.example.myhouse24admin.model.houses.FloorResponse;
import com.example.myhouse24admin.model.houses.HouseShortResponse;
import com.example.myhouse24admin.model.houses.SectionResponse;

import java.math.BigDecimal;

public class ApartmentResponse {
    private Long id;
    private Integer apartmentNumber;
    private HouseShortResponse house;
    private SectionResponse section;
    private FloorResponse floor;
    private ApartmentOwnerShortResponse owner;
    private BigDecimal balance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(Integer apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public HouseShortResponse getHouse() {
        return house;
    }

    public void setHouse(HouseShortResponse house) {
        this.house = house;
    }

    public SectionResponse getSection() {
        return section;
    }

    public void setSection(SectionResponse section) {
        this.section = section;
    }

    public FloorResponse getFloor() {
        return floor;
    }

    public void setFloor(FloorResponse floor) {
        this.floor = floor;
    }

    public ApartmentOwnerShortResponse getOwner() {
        return owner;
    }

    public void setOwner(ApartmentOwnerShortResponse owner) {
        this.owner = owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
