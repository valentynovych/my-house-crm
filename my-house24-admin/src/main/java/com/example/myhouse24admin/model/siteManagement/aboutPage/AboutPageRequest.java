package com.example.myhouse24admin.model.siteManagement.aboutPage;

import com.example.myhouse24admin.model.siteManagement.servicesPage.SeoRequest;
import com.example.myhouse24admin.validators.fileValidator.aboutPage.ImageNotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class AboutPageRequest {
    @NotBlank(message = "{validation-not-empty}")
    @Size(max = 100,message = "{validation-size-max}")
    private String title;
    @NotBlank(message = "{validation-not-empty}")
    @Size(max = 8000,message = "{validation-size-max}")
    private String aboutText;
    @NotBlank(message = "{validation-not-empty}")
    @Size(max = 1500,message = "{validation-size-max}")
    private String aboutTextWithoutTags;
    @Size(max = 100,message = "{validation-size-max}")
    private String additionalTitle;
    @Size(max = 4000,message = "{validation-size-max}")
    private String additionalText;
    @Size(max = 700,message = "{validation-size-max}")
    private String additionalTextWithoutTags;
    @ImageNotEmpty(message = "{validation-image-required}")
    private MultipartFile directorImage;
    private List<MultipartFile> newImages;
    private List<MultipartFile> additionalNewImages;
    private List<MultipartFile> newDocuments;
    private List<Long> galleryIdsToDelete;
    private List<Long> additionalGalleryIdsToDelete;
    private List<Long> documentIdsToDelete;
    private SeoRequest seoRequest;

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

    public String getAboutTextWithoutTags() {
        return aboutTextWithoutTags;
    }

    public void setAboutTextWithoutTags(String aboutTextWithoutTags) {
        this.aboutTextWithoutTags = aboutTextWithoutTags;
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

    public String getAdditionalTextWithoutTags() {
        return additionalTextWithoutTags;
    }

    public void setAdditionalTextWithoutTags(String additionalTextWithoutTags) {
        this.additionalTextWithoutTags = additionalTextWithoutTags;
    }

    public MultipartFile getDirectorImage() {
        return directorImage;
    }

    public void setDirectorImage(MultipartFile directorImage) {
        this.directorImage = directorImage;
    }

    public List<MultipartFile> getNewImages() {
        return newImages;
    }

    public void setNewImages(List<MultipartFile> newImages) {
        this.newImages = newImages;
    }

    public List<MultipartFile> getAdditionalNewImages() {
        return additionalNewImages;
    }

    public void setAdditionalNewImages(List<MultipartFile> additionalNewImages) {
        this.additionalNewImages = additionalNewImages;
    }

    public SeoRequest getSeoRequest() {
        return seoRequest;
    }

    public void setSeoRequest(SeoRequest seoRequest) {
        this.seoRequest = seoRequest;
    }

    public List<Long> getGalleryIdsToDelete() {
        return galleryIdsToDelete;
    }

    public void setGalleryIdsToDelete(List<Long> galleryIdsToDelete) {
        this.galleryIdsToDelete = galleryIdsToDelete;
    }

    public List<Long> getAdditionalGalleryIdsToDelete() {
        return additionalGalleryIdsToDelete;
    }

    public void setAdditionalGalleryIdsToDelete(List<Long> additionalGalleryIdsToDelete) {
        this.additionalGalleryIdsToDelete = additionalGalleryIdsToDelete;
    }

    public List<Long> getDocumentIdsToDelete() {
        return documentIdsToDelete;
    }

    public void setDocumentIdsToDelete(List<Long> documentIdsToDelete) {
        this.documentIdsToDelete = documentIdsToDelete;
    }

    public List<MultipartFile> getNewDocuments() {
        return newDocuments;
    }

    public void setNewDocuments(List<MultipartFile> newDocuments) {
        this.newDocuments = newDocuments;
    }
}
