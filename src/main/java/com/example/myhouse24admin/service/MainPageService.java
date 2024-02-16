package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageRequest;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageResponse;

public interface MainPageService {
    void createMainPageIfNotExist();
    MainPageResponse getMainPageResponse();
    void updateMainPage(MainPageRequest mainPageRequest);
}
