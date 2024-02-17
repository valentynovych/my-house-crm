package com.example.myhouse24admin.model.apartments;

import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerShortResponse;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountShortResponse;
import com.example.myhouse24admin.model.houses.FloorResponse;
import com.example.myhouse24admin.model.houses.HouseShortResponse;
import com.example.myhouse24admin.model.houses.SectionResponse;
import com.example.myhouse24admin.model.tariffs.TariffShortResponse;

public class ApartmentExtendResponse {
    private Long id;
    private Integer apartmentNumber;
    private HouseShortResponse house;
    private SectionResponse section;
    private FloorResponse floor;
    private ApartmentOwnerShortResponse owner;
    private TariffShortResponse tariff;
    private PersonalAccountShortResponse personalAccount;
    private Double area;

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

    public TariffShortResponse getTariff() {
        return tariff;
    }

    public void setTariff(TariffShortResponse tariff) {
        this.tariff = tariff;
    }

    public PersonalAccountShortResponse getPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(PersonalAccountShortResponse personalAccount) {
        this.personalAccount = personalAccount;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }
}
