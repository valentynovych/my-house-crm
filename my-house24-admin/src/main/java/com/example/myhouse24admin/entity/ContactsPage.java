package com.example.myhouse24admin.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contacts_page")
public class ContactsPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String title;
    @Column(length = 2000, nullable = false)
    private String text;
    @Column(name = "link_to_site", length = 200, nullable = false)
    private String linkToSite;
    @Column(name = "full_name", length = 200, nullable = false)
    private String fullName;
    @Column(length = 200, nullable = false)
    private String location;
    @Column(length = 200, nullable = false)
    private String address;
    @Column(name = "phone_number",length = 13, nullable = false, unique = true)
    private String phoneNumber;
    @Column(length = 100, nullable = false, unique = true)
    private String email;
    @Column(name = "map_code",length = 500, nullable = false)
    private String mapCode;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "seo_id", referencedColumnName = "id", nullable = false)
    private Seo seo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLinkToSite() {
        return linkToSite;
    }

    public void setLinkToSite(String linkToSite) {
        this.linkToSite = linkToSite;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMapCode() {
        return mapCode;
    }

    public void setMapCode(String mapCode) {
        this.mapCode = mapCode;
    }

    public Seo getSeo() {
        return seo;
    }

    public void setSeo(Seo seo) {
        this.seo = seo;
    }
}
