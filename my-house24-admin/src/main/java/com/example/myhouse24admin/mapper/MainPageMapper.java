package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.MainPage;
import com.example.myhouse24admin.entity.MainPageBlock;
import com.example.myhouse24admin.entity.Seo;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageBlockRequest;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageRequest;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MainPageMapper {
    @Mapping(target = "title", source = "value")
    @Mapping(target = "text", source = "value")
    @Mapping(target = "seo", source = "newSeo")
    MainPage createMainPage(String value, Seo newSeo);
    @Mapping(target = "seoTitle", source = "mainPage.seo.title")
    @Mapping(target = "seoDescription", source = "mainPage.seo.description")
    @Mapping(target = "seoKeywords", source = "mainPage.seo.keywords")
    @Mapping(target = "mainPageBlocks", source = "mainPageBlocks")
    MainPageResponse mainPageToMainPageResponse(MainPage mainPage, List<MainPageBlock> mainPageBlocks);
    @Mapping(ignore = true, target = "id")
    @Mapping(target = "image", source = "imageName")
    MainPageBlock createMainPageBlock(MainPageBlockRequest mainPageBlockRequest, String imageName);

    @Mapping(ignore = true, target = "id")
    @Mapping(target = "image", source = "imageName")
    void updateMainPageBlock(@MappingTarget MainPageBlock mainPageBlock, MainPageBlockRequest mainPageBlockRequest, String imageName);
    @Mapping(target = "seo.title", source = "mainPageRequest.seoRequest.seoTitle")
    @Mapping(target = "seo.description", source = "mainPageRequest.seoRequest.seoDescription")
    @Mapping(target = "seo.keywords", source = "mainPageRequest.seoRequest.seoKeywords")
    @Mapping(target = "image1", source = "image1Name")
    @Mapping(target = "image2", source = "image2Name")
    @Mapping(target = "image3", source = "image3Name")
    void updateMainPage(@MappingTarget MainPage mainPage, MainPageRequest mainPageRequest, String image1Name, String image2Name, String image3Name);
}
