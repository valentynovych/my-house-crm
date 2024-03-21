package com.example.mapper;

import com.example.entity.AboutPage;
import com.example.entity.AdditionalGallery;
import com.example.entity.Document;
import com.example.entity.Gallery;
import com.example.model.aboutPage.AboutPageResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AboutPageMapper {
    @Mapping(target = "gallery", source = "gallery")
    @Mapping(target = "additionalGallery", source = "additionalGallery")
    @Mapping(target = "documents", source = "documents")
    @Mapping(target = "title", source = "aboutPage.title")
    @Mapping(target = "aboutText", source = "aboutPage.aboutText")
    @Mapping(target = "additionalTitle", source = "aboutPage.additionalTitle")
    @Mapping(target = "additionalText", source = "aboutPage.additionalText")
    AboutPageResponse aboutPageToAboutPageResponse(AboutPage aboutPage, List<Gallery> gallery,
                                                   List<AdditionalGallery> additionalGallery,
                                                   List<Document> documents);
}
