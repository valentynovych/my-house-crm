package com.example.myhouse24admin.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tariffs")
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(length = 350, nullable = false)
    private String description;
    @Column(name = "last_modify", nullable = false)
    private Instant lastModify;
    private boolean deleted;
    @OneToMany(mappedBy = "tariff",
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REMOVE})
    private List<TariffItem> tariffItems = new ArrayList<>();

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

    public Instant getLastModify() {
        return lastModify;
    }

    public void setLastModify(Instant lastModify) {
        this.lastModify = lastModify;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<TariffItem> getTariffItems() {
        return tariffItems;
    }

    public void setTariffItems(List<TariffItem> tariffItems) {
        this.tariffItems = tariffItems;
    }
}
