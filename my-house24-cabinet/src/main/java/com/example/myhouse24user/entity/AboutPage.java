package com.example.myhouse24user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "about_page")
public class AboutPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String title;
    @Column(name = "about_text",length = 500, nullable = false)
    private String aboutText;
    @Column(name = "additional_title", length = 100)
    private String additionalTitle;
    @Column(name = "additional_text",length = 500)
    private String additionalText;
    @Column(name = "director_image",length = 200)
    private String directorImage;
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

    public String getAboutText() {
        return aboutText;
    }

    public void setAboutText(String aboutText) {
        this.aboutText = aboutText;
    }

    public String getAdditionalTitle() {
        return additionalTitle;
    }

    public void setAdditionalTitle(String additionalTitle) {
        this.additionalTitle = additionalTitle;
    }

    public String getAdditionalText() {
        return additionalText;
    }

    public void setAdditionalText(String additionalText) {
        this.additionalText = additionalText;
    }

    public String getDirectorImage() {
        return directorImage;
    }

    public void setDirectorImage(String directorImage) {
        this.directorImage = directorImage;
    }

    public Seo getSeo() {
        return seo;
    }

    public void setSeo(Seo seo) {
        this.seo = seo;
    }
}
