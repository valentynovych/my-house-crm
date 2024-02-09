package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.House;
import com.example.myhouse24admin.entity.Section;
import com.example.myhouse24admin.model.houses.SectionRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SectionMapper {

    Section sectionRequestToSection(SectionRequest sectionRequest);

//    @Mapping(target = "house", source = "house")
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "name", ignore = true)
//    @Mapping(target = "deleted", ignore = true)
//    Section updateSession_HouseFromHouse(@MappingTarget Section section, House house);

    House updateSessionList_HouseFromHouse(List<Section> sections, House house);

    SectionRequest sectionToSectionRequest(Section section);
}
