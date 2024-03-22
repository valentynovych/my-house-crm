package com.example.myhouse24rest.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "services_page")
public class ServicesPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "seo_id", referencedColumnName = "id", nullable = false)
    private Seo seo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Seo getSeo() {
        return seo;
    }

    public void setSeo(Seo seo) {
        this.seo = seo;
    }
}
