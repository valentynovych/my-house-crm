package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.roles.PermissionResponse;
import com.example.myhouse24admin.model.roles.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse createRoleResponse();
    void updatePermissions(boolean[] managerPermissions, boolean[] accountantPermissions,
                           boolean[] electricianPermissions, boolean[] plumberPermissions);
    List<PermissionResponse> getPermissionResponsesByRole(String role);
}
