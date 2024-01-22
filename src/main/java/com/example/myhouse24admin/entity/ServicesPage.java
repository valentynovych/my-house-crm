package com.example.myhouse24admin.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "services_page")
public class ServicesPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "seo_id", referencedColumnName = "id", nullable = false)
    private SEO seo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SEO getSeo() {
        return seo;
    }

    public void setSeo(SEO seo) {
        this.seo = seo;
    }
}
