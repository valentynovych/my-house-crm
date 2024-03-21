package com.example.myhouse24admin.model.siteManagement.mainPage;

import com.example.myhouse24admin.entity.MainPageBlock;
import com.example.myhouse24admin.model.siteManagement.servicesPage.SeoRequest;
import com.example.myhouse24admin.validators.fileValidator.mainPage.image1.Image1NotEmpty;
import com.example.myhouse24admin.validators.fileValidator.mainPage.image2.Image2NotEmpty;
import com.example.myhouse24admin.validators.fileValidator.mainPage.image3.Image3NotEmpty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class MainPageRequest {
    @NotBlank(message = "{validation-not-empty}")
    @Size(max = 100,message = "{validation-size-max}")
    private String title;
    @NotBlank(message = "{validation-not-empty}")
    @Size(max = 500,message = "{validation-size-max}")
    private String text;
    private boolean showLinks;
    @Image1NotEmpty(message = "{validation-image-required}")
    private MultipartFile image1;
    @Image2NotEmpty(message = "{validation-image-required}")
    private MultipartFile image2;
    @Image3NotEmpty(message = "{validation-image-required}")
    private MultipartFile image3;
    @Valid
    private List<MainPageBlockRequest> mainPageBlocks;
    private List<Long> idsToDelete;
    private SeoRequest seoRequest;

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

    public MultipartFile getImage1() {
        return image1;
    }

    public void setImage1(MultipartFile image1) {
        this.image1 = image1;
    }

    public MultipartFile getImage2() {
        return image2;
    }

    public void setImage2(MultipartFile image2) {
        this.image2 = image2;
    }

    public MultipartFile getImage3() {
        return image3;
    }

    public void setImage3(MultipartFile image3) {
        this.image3 = image3;
    }

    public List<MainPageBlockRequest> getMainPageBlocks() {
        return mainPageBlocks;
    }

    public void setMainPageBlocks(List<MainPageBlockRequest> mainPageBlocks) {
        this.mainPageBlocks = mainPageBlocks;
    }

    public List<Long> getIdsToDelete() {
        return idsToDelete;
    }

    public void setIdsToDelete(List<Long> idsToDelete) {
        this.idsToDelete = idsToDelete;
    }

    public SeoRequest getSeoRequest() {
        return seoRequest;
    }

    public void setSeoRequest(SeoRequest seoRequest) {
        this.seoRequest = seoRequest;
    }
}
