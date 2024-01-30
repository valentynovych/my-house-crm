package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Endpoint;
import com.example.myhouse24admin.entity.Permission;
import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.model.roles.PermissionResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface PermissionMapper {
    List<Boolean> permissionsListToAllowancesList(List<Permission> permissions);
    default Boolean permissionToAllowance(Permission permission){
        return permission.isAllowed();
    }

    List<PermissionResponse> permissionListToPermissionResponseList(List<Permission> permissions);
    @Mapping(target = "endpoint", source = "endpoint.endpoint")
    PermissionResponse permissionToPermissionResponse(Permission permission);
    @Mapping(target = "endpoint", source = "endpoint")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "allowed", source = "allowed")
    @Mapping(ignore = true, target = "id")
    Permission createPermission(Role role, Endpoint endpoint, boolean allowed);
}
