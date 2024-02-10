package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.House;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.houses.*;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {SectionMapper.class, FloorMapper.class, StaffMapper.class})
public interface HouseMapper {


    @Mapping(target = "staff", source = "staffIds", qualifiedByName = "staffIdsToStaff")
    House houseAddRequestToHouse(HouseAddRequest houseAddRequest);

    @Named(value = "staffIdsToStaff")
    static List<Staff> staffIdsToStaff(List<Long> staffIds) {
        List<Staff> staffList = new ArrayList<>();
        for (Long id : staffIds) {
            Staff staff = new Staff();
            staff.setId(id);
            staffList.add(staff);
        }
        return staffList;
    }

    List<HouseShortResponse> houseListToHouseShortResponseList(List<House> houses);

    HouseShortResponse houseToHouseShortResponse(House house);

    @Mapping(target = "sectionsCount", expression = "java(house.getSections().size())")
    @Mapping(target = "floorsCount", expression = "java(house.getFloors().size())")
    HouseViewResponse houseToHouseViewResponse(House house);

    HouseResponse houseToHouseResponse(House house);

    void updateHouseFromHouseRequest(@MappingTarget House house, HouseEditRequest houseEditRequest);
}
