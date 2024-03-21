package com.example.model.aboutPage;

import com.example.entity.AdditionalGallery;
import com.example.entity.Document;
import com.example.entity.Gallery;

import java.util.List;

public class AboutPageResponse {
    private String title;
    private String aboutText;
    private String additionalTitle;
    private String additionalText;
    private String directorImage;
    private List<Gallery> gallery;
    private List<AdditionalGallery> additionalGallery;
    private List<Document> documents;

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

    public List<Gallery> getGallery() {
        return gallery;
    }

    public void setGallery(List<Gallery> gallery) {
        this.gallery = gallery;
    }

    public List<AdditionalGallery> getAdditionalGallery() {
        return additionalGallery;
    }

    public void setAdditionalGallery(List<AdditionalGallery> additionalGallery) {
        this.additionalGallery = additionalGallery;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
