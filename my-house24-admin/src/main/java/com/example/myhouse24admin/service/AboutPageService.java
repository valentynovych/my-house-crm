package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageRequest;
import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageResponse;

public interface AboutPageService {
    AboutPageResponse getAboutPageResponse();
    void createAboutPageIfNotExist();
    void updateAboutPage(AboutPageRequest aboutPageRequest);
}
