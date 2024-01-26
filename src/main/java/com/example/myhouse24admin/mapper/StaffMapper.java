package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Language;
import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.staff.StaffEditRequest;
import com.example.myhouse24admin.model.staff.StaffResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface StaffMapper {
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "firstName", source = "name")
    @Mapping(target = "lastName", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "language", source = "language")
    Staff createFirstStaff(String email, String password, String name, String phoneNumber, Role role, Language language);

    @Mapping(target = "role.id", source = "roleId")
    Staff staffEditRequestToStaff(StaffEditRequest staffEditRequest);

    StaffResponse staffToStaffResponse(Staff staffList);

    List<StaffResponse> staffListToStaffResponseList(List<Staff> staffList);

    @Mapping(target = "role", source = "roleId", qualifiedByName = "setNewRole")
    void updateWithPassword(@MappingTarget Staff staff, StaffEditRequest staffEditRequest);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", source = "roleId", qualifiedByName = "setNewRole")
    void updateWithoutPassword(@MappingTarget Staff staff, StaffEditRequest staffEditRequest);

    @Named(value = "setNewRole")
    static Role setNewRole(Long roleId) {
        Role role = new Role();
        role.setId(roleId);
        return role;
    }
}
