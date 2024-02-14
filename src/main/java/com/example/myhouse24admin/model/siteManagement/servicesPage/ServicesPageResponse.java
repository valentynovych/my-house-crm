package com.example.myhouse24admin.model.siteManagement.servicesPage;

import com.example.myhouse24admin.entity.ServicePageBlock;

import java.util.List;

public record ServicesPageResponse(
        List<ServicePageBlock> servicePageBlocks,
        String seoTitle,
        String seoDescription,
        String seoKeywords
) {
}
