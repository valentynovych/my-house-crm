package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.ContactsPage;
import com.example.myhouse24admin.entity.Seo;
import com.example.myhouse24admin.model.siteManagement.contacts.ContactsPageDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface ContactsPageMapper {
    @Mapping(target = "seoTitle", source = "seo.title")
    @Mapping(target = "seoDescription", source = "seo.description")
    @Mapping(target = "seoKeywords", source = "seo.keywords")
    ContactsPageDto contactsPageToContactsPageResponse(ContactsPage contactsPage);
    @Mapping(target = "title", source = "value")
    @Mapping(target = "text", source = "value")
    @Mapping(target = "linkToSite", source = "value")
    @Mapping(target = "fullName", source = "value")
    @Mapping(target = "location", source = "value")
    @Mapping(target = "address", source = "value")
    @Mapping(target = "phoneNumber", source = "value")
    @Mapping(target = "email", source = "value")
    @Mapping(target = "mapCode", source = "value")
    @Mapping(target = "seo", source = "newSeo")
    ContactsPage createContactsPage(String value, Seo newSeo);
    @Mapping(target = "seo.title", source = "seoTitle")
    @Mapping(target = "seo.description", source = "seoDescription")
    @Mapping(target = "seo.keywords", source = "seoKeywords")
    void setContactsPage (@MappingTarget ContactsPage contactsPage, ContactsPageDto contactsPageDto);
}
