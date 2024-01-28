package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.model.roles.RoleResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface RoleMapper {
    @Mapping(target = "managerAllowances", source = "managerAllowances")
    @Mapping(target = "accountantAllowances", source = "accountantAllowances")
    @Mapping(target = "electricianAllowances", source = "electricianAllowances")
    @Mapping(target = "plumberAllowances", source = "plumberAllowances")
    RoleResponse createRoleResponse(List<Boolean> managerAllowances,
                                    List<Boolean> accountantAllowances,
                                    List<Boolean> electricianAllowances,
                                    List<Boolean> plumberAllowances);
}
