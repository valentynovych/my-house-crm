package com.example.model.mainPage;

import com.example.entity.MainPageBlock;

import java.util.List;

public class MainPageResponse {
    private String title;
    private String text;
    private boolean showLinks;
    private String image1;
    private String image2;
    private String image3;
    private ContactsResponse contactsResponse;
    private List<MainPageBlock> mainPageBlocks;

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

    public ContactsResponse getContactsResponse() {
        return contactsResponse;
    }

    public void setContactsResponse(ContactsResponse contactsResponse) {
        this.contactsResponse = contactsResponse;
    }

    public List<MainPageBlock> getMainPageBlocks() {
        return mainPageBlocks;
    }

    public void setMainPageBlocks(List<MainPageBlock> mainPageBlocks) {
        this.mainPageBlocks = mainPageBlocks;
    }
}
