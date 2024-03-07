package com.example.myhouse24admin.model.siteManagement.servicesPage;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

public class ServicePageRequest {
    @Valid
    private List<ServicePageBlockRequest> servicePageBlocks = new ArrayList<>();
    private List<Long> idsToDelete;
    private SeoRequest seoRequest;


    public List<ServicePageBlockRequest> getServicePageBlocks() {
        return servicePageBlocks;
    }

    public void setServicePageBlocks(List<ServicePageBlockRequest> servicePageBlocks) {
        this.servicePageBlocks = servicePageBlocks;
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
