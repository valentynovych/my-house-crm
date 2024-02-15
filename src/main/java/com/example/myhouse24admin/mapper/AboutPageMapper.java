package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.AboutPage;
import com.example.myhouse24admin.entity.AdditionalGallery;
import com.example.myhouse24admin.entity.Gallery;
import com.example.myhouse24admin.entity.Seo;
import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageRequest;
import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface AboutPageMapper {
    @Mapping(target = "title", source = "value")
    @Mapping(target = "aboutText", source = "value")
    @Mapping(target = "seo", source = "newSeo")
    AboutPage createAboutPage(String value, Seo newSeo);
    @Mapping(target = "seoTitle", source = "aboutPage.seo.title")
    @Mapping(target = "seoDescription", source = "aboutPage.seo.description")
    @Mapping(target = "seoKeywords", source = "aboutPage.seo.keywords")
    @Mapping(target = "gallery", source = "gallery")
    @Mapping(target = "additionalGallery", source = "additionalGallery")
    AboutPageResponse aboutPageToAboutPageResponse(AboutPage aboutPage, List<Gallery> gallery, List<AdditionalGallery> additionalGallery);
    @Mapping(target = "seo.title", source = "aboutPageRequest.seoRequest.seoTitle")
    @Mapping(target = "seo.description", source = "aboutPageRequest.seoRequest.seoDescription")
    @Mapping(target = "seo.keywords", source = "aboutPageRequest.seoRequest.seoKeywords")
    @Mapping(target = "directorImage", source = "imageName")
    void updateAboutPage(@MappingTarget AboutPage aboutPage,
                         AboutPageRequest aboutPageRequest,
                         String imageName);
}
