package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.Staff;
import com.example.myhouse24user.model.staff.StaffShortResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StaffMapper {

    @Mapping(target = "fullName", expression = "java(staff.getFirstName() + \" \" + staff.getLastName())")
    StaffShortResponse staffToStaffShortResponse(Staff staff);
}
