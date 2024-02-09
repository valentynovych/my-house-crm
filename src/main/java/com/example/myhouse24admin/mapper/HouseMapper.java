package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.House;
import com.example.myhouse24admin.entity.Section;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.houses.HouseAddRequest;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {SectionMapper.class, FloorMapper.class})
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
}
