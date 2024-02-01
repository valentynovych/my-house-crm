package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Endpoint;
import com.example.myhouse24admin.entity.Permission;
import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.mapper.PermissionMapper;
import com.example.myhouse24admin.mapper.RoleMapper;
import com.example.myhouse24admin.model.roles.PermissionResponse;
import com.example.myhouse24admin.model.roles.RoleResponse;
import com.example.myhouse24admin.repository.EndpointRepo;
import com.example.myhouse24admin.repository.PermissionRepo;
import com.example.myhouse24admin.repository.RoleRepo;
import com.example.myhouse24admin.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import static com.example.myhouse24admin.specification.PermissionSpecification.*;
@Service
public class RoleServiceImpl implements RoleService {
    private final Logger logger = LogManager.getLogger(RoleServiceImpl.class);
    private final PermissionRepo permissionRepo;
    private final RoleRepo roleRepo;
    private final EndpointRepo endpointRepo;
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(PermissionRepo permissionRepo, RoleRepo roleRepo,
                           EndpointRepo endpointRepo,
                           PermissionMapper permissionMapper,
                           RoleMapper roleMapper) {
        this.permissionRepo = permissionRepo;
        this.roleRepo = roleRepo;
        this.endpointRepo = endpointRepo;
        this.permissionMapper = permissionMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public RoleResponse createRoleResponse() {
        logger.info("createRoleResponse() - Creating role response");
        List<Permission> managerPermissions = permissionRepo.findAll(byRoleId(2L));
        List<Permission> accountantPermissions = permissionRepo.findAll(byRoleId(3L));
        List<Permission> electricianPermissions = permissionRepo.findAll(byRoleId(4L));
        List<Permission> plumberPermissions = permissionRepo.findAll(byRoleId(5L));
        RoleResponse roleResponse = createRolesResponse(managerPermissions, accountantPermissions, electricianPermissions, plumberPermissions);
        logger.info("createRoleResponse() - Role response was created");
        return roleResponse;
    }

    private RoleResponse createRolesResponse(List<Permission> managerPermissions,
                                             List<Permission> accountantPermissions,
                                             List<Permission> electricianPermissions,
                                             List<Permission> plumberPermissions) {
        List<Boolean> managerAllowances = permissionMapper.permissionsListToAllowancesList(managerPermissions);
        List<Boolean> accountantAllowances = permissionMapper.permissionsListToAllowancesList(accountantPermissions);
        List<Boolean> electricianAllowances = permissionMapper.permissionsListToAllowancesList(electricianPermissions);
        List<Boolean> plumberAllowances = permissionMapper.permissionsListToAllowancesList(plumberPermissions);
        return roleMapper.createRoleResponse(managerAllowances, accountantAllowances, electricianAllowances, plumberAllowances);
    }

    @Override
    public List<PermissionResponse> getPermissionResponsesByRole(String role) {
        logger.info("getPermissionResponsesByRole() - Getting permission responses by role "+role);
        String[] roles = role.split("_");
        List<Permission> permissions = permissionRepo.findAllByRoleName(roles[1]);
        List<PermissionResponse> permissionResponses = permissionMapper.permissionListToPermissionResponseList(permissions);
        logger.info("getPermissionResponsesByRole() - Permission responses was got");
        return permissionResponses;
    }

    @Override
    public void updatePermissions(boolean[] managerPermissions, boolean[] accountantPermissions, boolean[] electricianPermissions, boolean[] plumberPermissions) {
        updateManagerPermissions(managerPermissions);
        updateAccountantPermissions(accountantPermissions);
        updateElectricianPermissions(electricianPermissions);
        updatePlumberPermissions(plumberPermissions);
    }
    private void updateManagerPermissions(boolean[] managerPermissions) {
        List<Permission> permissions = permissionRepo.findAll(byRoleId(2L), Sort.by("id"));
        int i = 0;
        for(Permission p: permissions){
            p.setAllowed(managerPermissions[i]);
            i++;
        }
        permissionRepo.saveAll(permissions);
    }
    private void updateAccountantPermissions(boolean[] accountantPermissions) {
        List<Permission> permissions = permissionRepo.findAll(byRoleId(3L), Sort.by("id"));
        int i = 0;
        for(Permission p: permissions){
            p.setAllowed(accountantPermissions[i]);
            i++;
        }
        permissionRepo.saveAll(permissions);
    }
    private void updateElectricianPermissions(boolean[] electricianPermissions) {
        List<Permission> permissions = permissionRepo.findAll(byRoleId(4L), Sort.by("id"));
        int i = 0;
        for (Permission p : permissions) {
            p.setAllowed(electricianPermissions[i]);
            i++;
        }
        permissionRepo.saveAll(permissions);
    }
    private void updatePlumberPermissions(boolean[] plumberPermissions) {
        List<Permission> permissions = permissionRepo.findAll(byRoleId(5L), Sort.by("id"));
        int i = 0;
        for (Permission p : permissions) {
            p.setAllowed(plumberPermissions[i]);
            i++;
        }
        permissionRepo.saveAll(permissions);
    }

    @Override
    public String getAllowedEndPoint(String email) {
        logger.info("getAllowedEndPoint() - Getting endpoint that allowed by staff email "+email);
        List<Permission> permissions = permissionRepo.findByStaffEmailThatAllowed(email);
        String endpoint = permissions.get(0).getEndpoint().getEndpoint();
        logger.info("getAllowedEndPoint() - Endpoint was got");
        return endpoint;
    }

    @Override
    public void createPermissions() {
        logger.info("createPermissions() - Creating permissions");
        if (isTableEmpty()) {
            List<Permission> permissions = new ArrayList<>();
            List<Endpoint> endpoints = endpointRepo.findAll(Sort.by("id"));
            List<Role> roles = roleRepo.findAll(Sort.by("id"));
            for (Role role : roles) {
                for (Endpoint endpoint : endpoints) {
                    boolean allowed = isAllowed(role, endpoint);
                    Permission permission = permissionMapper.createPermission(role, endpoint, allowed);
                    permissions.add(permission);
                }
            }
            permissionRepo.saveAll(permissions);
            logger.info("createPermissions() - Permissions were created");
        } else {
            logger.info("createPermissions() - Permissions have already been created");
        }
    }

    private boolean isTableEmpty() {
        return permissionRepo.count() == 0;
    }


    private static boolean isAllowed(Role role, Endpoint endpoint) {
        if ((role.getId().equals(2L) && endpoint.getId().equals(14L))
                || (role.getId().equals(3L) && endpoint.getId() >= 4L && endpoint.getId() <= 7L)
                || (role.getId().equals(3L) && endpoint.getId() >= 9L && endpoint.getId() <= 15L)
                || ((role.getId().equals(4L) || role.getId().equals(5L)) && (endpoint.getId() >= 2L && endpoint.getId() <= 7L))
                || ((role.getId().equals(4L) || role.getId().equals(5L)) && (endpoint.getId() >= 10L && endpoint.getId() <= 17L))
                || ((role.getId().equals(4L) || role.getId().equals(5L)) && (endpoint.getId() >= 4L && endpoint.getId() <= 7L))) {
            return false;
        } else {
             return true;
        }
    }
}
