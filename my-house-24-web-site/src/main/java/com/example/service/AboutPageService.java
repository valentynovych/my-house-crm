package com.example.service;

import com.example.model.aboutPage.AboutPageResponse;

public interface AboutPageService {
    AboutPageResponse getAboutPageResponse();
    byte[] getDocument(String documentName);
}
