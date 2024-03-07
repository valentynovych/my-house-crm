package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Seo;
import com.example.myhouse24admin.entity.ServicePageBlock;
import com.example.myhouse24admin.entity.ServicesPage;
import com.example.myhouse24admin.model.siteManagement.servicesPage.SeoRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicePageBlockRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicesPageResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface ServicesPageMapper {
    @Mapping(target = "seo", source = "newSeo")
    ServicesPage createServicesPage(Seo newSeo);

    @Mapping(target = "seoTitle", source = "servicesPage.seo.title")
    @Mapping(target = "seoDescription", source = "servicesPage.seo.description")
    @Mapping(target = "seoKeywords", source = "servicesPage.seo.keywords")
    ServicesPageResponse servicesPageToServicesPageResponse(ServicesPage servicesPage,
                                                            List<ServicePageBlock> servicePageBlocks);

    @Mapping(target = "title", source = "value")
    @Mapping(target = "description", source = "value")
    ServicePageBlock createFirstServicePageBlock(String value);
    @Mapping(ignore = true, target = "id")
    @Mapping(target = "image", source = "imageName")
    void updateServicePageBlock(@MappingTarget ServicePageBlock ServicePageBlock, ServicePageBlockRequest servicePageBlockRequest, String imageName);
    @Mapping(target = "seo.title", source = "seoTitle")
    @Mapping(target = "seo.description", source = "seoDescription")
    @Mapping(target = "seo.keywords", source = "seoKeywords")
    void updateServicesPage(@MappingTarget ServicesPage servicesPage, SeoRequest seoRequest);
    @Mapping(ignore = true, target = "id")
    @Mapping(target = "image", source = "imageName")
    ServicePageBlock createServicePageBlock(ServicePageBlockRequest servicePageBlockRequest, String imageName);
}