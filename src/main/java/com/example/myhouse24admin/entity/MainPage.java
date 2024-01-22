package com.example.myhouse24admin.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "main_page")
public class MainPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String title;
    @Column(length = 500, nullable = false)
    private String text;
    @Column(name = "show_links", nullable = false)
    private boolean showLinks;
    @Column(length = 200)
    private String image1;
    @Column(length = 200)
    private String image2;
    @Column(length = 200)
    private String image3;
    @OneToOne
    @JoinColumn(name = "seo_id", referencedColumnName = "id", nullable = false)
    private SEO seo;

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

    public boolean isShowLinks() {
        return showLinks;
    }

    public void setShowLinks(boolean showLinks) {
        this.showLinks = showLinks;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public SEO getSeo() {
        return seo;
    }

    public void setSeo(SEO seo) {
        this.seo = seo;
    }
}
