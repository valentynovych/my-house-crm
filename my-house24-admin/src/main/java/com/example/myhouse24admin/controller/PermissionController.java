package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.roles.PermissionResponse;
import com.example.myhouse24admin.model.roles.RoleResponse;
import com.example.myhouse24admin.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class PermissionController {
    private final RoleService roleService;

    public PermissionController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/getPermissions")
    public @ResponseBody List<PermissionResponse> getRoleResponse(@RequestParam("role") String role){
        return roleService.getPermissionResponsesByRole(role);
    }
}
