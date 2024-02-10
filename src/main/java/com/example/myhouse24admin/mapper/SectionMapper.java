package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.House;
import com.example.myhouse24admin.entity.Section;
import com.example.myhouse24admin.model.houses.SectionRequest;
import com.example.myhouse24admin.model.houses.SectionResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SectionMapper {

    Section sectionRequestToSection(SectionRequest sectionRequest);

    House updateSessionList_HouseFromHouse(List<Section> sections, House house);

    SectionRequest sectionToSectionRequest(Section section);

    SectionResponse sectionToSectionResponse(Section section);
}
