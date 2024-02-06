package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Endpoint;
import com.example.myhouse24admin.entity.Permission;
import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.mapper.PermissionMapper;
import com.example.myhouse24admin.model.roles.PermissionResponse;
import com.example.myhouse24admin.model.roles.RoleResponse;
import com.example.myhouse24admin.repository.EndpointRepo;
import com.example.myhouse24admin.repository.PermissionRepo;
import com.example.myhouse24admin.repository.RoleRepo;
import com.example.myhouse24admin.service.RoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final Logger logger = LogManager.getLogger(RoleServiceImpl.class);
    private final PermissionRepo permissionRepo;
    private final RoleRepo roleRepo;
    private final EndpointRepo endpointRepo;
    private final PermissionMapper permissionMapper;

    public RoleServiceImpl(PermissionRepo permissionRepo, RoleRepo roleRepo,
                           EndpointRepo endpointRepo,
                           PermissionMapper permissionMapper) {
        this.permissionRepo = permissionRepo;
        this.roleRepo = roleRepo;
        this.endpointRepo = endpointRepo;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public RoleResponse createRoleResponse() {
        logger.info("createRoleResponse() - Creating role response");
        List<List<Permission>> permissions = getPermissionsForEachRole();
        RoleResponse roleResponse = createRolesResponse(permissions);
        logger.info("createRoleResponse() - Role response was created");
        return roleResponse;
    }

    private RoleResponse createRolesResponse(List<List<Permission>> permissions) {
        List<List<Boolean>> allowances = new ArrayList<>();
        for(List<Permission> rolePermissions: permissions){
            List<Boolean> roleAllowances = permissionMapper.permissionsListToAllowancesList(rolePermissions);
            allowances.add(roleAllowances);
        }
        return new RoleResponse(allowances.get(0), allowances.get(1), allowances.get(2),allowances.get(3));
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
        logger.info("updatePermissions() - Updating permissions");
        List<List<Permission>> permissions = getPermissionsForEachRole();
        updatePermissions(permissions, List.of(managerPermissions,accountantPermissions, electricianPermissions, plumberPermissions));
        logger.info("updatePermissions() - Permissions were updated");
    }

    private void updatePermissions(List<List<Permission>> permissions, List<boolean[]> permissionRequest) {
        for(int i = 0; i < permissions.size(); i++){
            int j = 0;
            for(Permission p: permissions.get(i)){
                p.setAllowed(permissionRequest.get(i)[j]);
                j++;
            }
            permissionRepo.saveAll(permissions.get(i));
        }
    }
    private void updateAccountantPermissions(boolean[] accountantPermissions) {
        List<Permission> permissions = permissionRepo.findAllByRoleName("ACCOUNTANT");
        int i = 0;
        for(Permission p: permissions){
            p.setAllowed(accountantPermissions[i]);
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

    private List<List<Permission>> getPermissionsForEachRole(){
        List<Permission> managerPermissions = permissionRepo.findAllByRoleName("MANAGER");
        List<Permission> accountantPermissions = permissionRepo.findAllByRoleName("ACCOUNTANT");
        List<Permission> electricianPermissions = permissionRepo.findAllByRoleName("ELECTRICIAN");
        List<Permission> plumberPermissions = permissionRepo.findAllByRoleName("PLUMBER");
        return List.of(managerPermissions, accountantPermissions, electricianPermissions, plumberPermissions);
    }
}
