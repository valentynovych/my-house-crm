package com.example.myhouse24user.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "apartments")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "apartment_number", length = 5, nullable = false)
    private String apartmentNumber;
    @Column(nullable = false)
    private double area;
    @Column(nullable = false)
    private boolean deleted;
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private ApartmentOwner owner;
    @ManyToOne
    @JoinColumn(name = "house_id", referencedColumnName = "id", nullable = false)
    private House house;
    @ManyToOne
    @JoinColumn(name = "section_id", referencedColumnName = "id", nullable = false)
    private Section section;
    @ManyToOne
    @JoinColumn(name = "floor_id", referencedColumnName = "id", nullable = false)
    private Floor floor;
    @ManyToOne
    @JoinColumn(name = "tariff_id", referencedColumnName = "id", nullable = false)
    private Tariff tariff;
    @OneToOne(mappedBy = "apartment", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private PersonalAccount personalAccount;
    @Column(nullable = false)
    private BigDecimal balance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public ApartmentOwner getOwner() {
        return owner;
    }

    public void setOwner(ApartmentOwner owner) {
        this.owner = owner;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public PersonalAccount getPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(PersonalAccount personalAccount) {
        this.personalAccount = personalAccount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
