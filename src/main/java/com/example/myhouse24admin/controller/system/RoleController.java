package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.model.roles.RoleResponse;
import com.example.myhouse24admin.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/system-settings/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ModelAndView getRolesPage() {
        return new ModelAndView("system/roles/roles");
    }

    @GetMapping("/getRoles")
    public @ResponseBody RoleResponse getRoleResponse(){
        return roleService.createRoleResponse();
    }
    @PostMapping("/update")
    public @ResponseBody ResponseEntity<?>updateRoles(@RequestParam("managerPermissions[]") boolean[] managerPermissions,
                                                      @RequestParam("accountantPermissions[]") boolean[] accountantPermissions,
                                                      @RequestParam("electricianPermissions[]") boolean[] electricianPermissions,
                                                      @RequestParam("plumberPermissions[]") boolean[] plumberPermissions
                                                      ) {
        roleService.updatePermissions(managerPermissions, accountantPermissions, electricianPermissions, plumberPermissions);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
