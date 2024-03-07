package com.example.myhouse24admin.model.siteManagement.mainPage;

import com.example.myhouse24admin.entity.MainPageBlock;

import java.util.List;

public class MainPageResponse {
    private String title;
    private String text;
    private boolean showLinks;
    private String image1;
    private String image2;
    private String image3;
    private List<MainPageBlock> mainPageBlocks;
    private String seoTitle;
    private String seoDescription;
    private String seoKeywords;

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

    public List<MainPageBlock> getMainPageBlocks() {
        return mainPageBlocks;
    }

    public void setMainPageBlocks(List<MainPageBlock> mainPageBlocks) {
        this.mainPageBlocks = mainPageBlocks;
    }

    public String getSeoTitle() {
        return seoTitle;
    }

    public void setSeoTitle(String seoTitle) {
        this.seoTitle = seoTitle;
    }

    public String getSeoDescription() {
        return seoDescription;
    }

    public void setSeoDescription(String seoDescription) {
        this.seoDescription = seoDescription;
    }

    public String getSeoKeywords() {
        return seoKeywords;
    }

    public void setSeoKeywords(String seoKeywords) {
        this.seoKeywords = seoKeywords;
    }
}
