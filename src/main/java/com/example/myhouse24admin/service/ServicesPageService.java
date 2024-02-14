package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.siteManagement.servicesPage.SeoRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicePageBlockRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicePageRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicesPageResponse;

public interface ServicesPageService {
    void createServicesPageIfNotExist();
    ServicesPageResponse getServicesPageResponse();
    void updateServicesPage(ServicePageRequest servicePageRequest);
}
