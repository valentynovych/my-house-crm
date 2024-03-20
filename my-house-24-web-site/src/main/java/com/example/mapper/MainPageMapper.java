package com.example.mapper;

import com.example.entity.ContactsPage;
import com.example.entity.MainPage;
import com.example.entity.MainPageBlock;
import com.example.model.mainPage.MainPageResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MainPageMapper {
    @Mapping(target = "mainPageBlocks", source = "mainPageBlocks")
    @Mapping(target = "text", source = "mainPage.text")
    @Mapping(target = "title", source = "mainPage.title")
    @Mapping(target = "contactsResponse.fullName", source = "contactsPage.fullName")
    @Mapping(target = "contactsResponse.location", source = "contactsPage.location")
    @Mapping(target = "contactsResponse.address", source = "contactsPage.address")
    @Mapping(target = "contactsResponse.phoneNumber", source = "contactsPage.phoneNumber")
    @Mapping(target = "contactsResponse.email", source = "contactsPage.email")
    MainPageResponse mainPageToMainPageResponse(MainPage mainPage,
                                                List<MainPageBlock> mainPageBlocks,
                                                ContactsPage contactsPage);
}
