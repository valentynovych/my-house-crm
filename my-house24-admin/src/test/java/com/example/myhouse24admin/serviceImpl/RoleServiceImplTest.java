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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    @Mock
    private PermissionRepo permissionRepo;
    @Mock
    private RoleRepo roleRepo;
    @Mock
    private EndpointRepo endpointRepo;
    @Mock
    private PermissionMapper permissionMapper;
    @InjectMocks
    private RoleServiceImpl roleService;
    private static Permission permission;
    @BeforeAll
    public static void setUp() {
        permission = new Permission();
        Endpoint endpoint = new Endpoint();
        endpoint.setEndpoint("endpoint");
        permission.setEndpoint(endpoint);
    }

    @Test
    void createRoleResponse() {
        when(permissionRepo.findAllByRoleName(anyString())).thenReturn(List.of(permission));
        when(permissionMapper.permissionsListToAllowancesList(anyList()))
                .thenReturn(List.of(true));

        RoleResponse roleResponse = roleService.createRoleResponse();
        assertThat(roleResponse.accountantAllowances()).hasSize(1);
        assertThat(roleResponse.managerAllowances()).hasSize(1);
        assertThat(roleResponse.plumberAllowances()).hasSize(1);
        assertThat(roleResponse.electricianAllowances()).hasSize(1);

        verify(permissionRepo, times(4)).findAllByRoleName(anyString());
        verify(permissionMapper, times(4))
                .permissionsListToAllowancesList(anyList());

        verifyNoMoreInteractions(permissionRepo);
        verifyNoMoreInteractions(permissionMapper);
    }

    @Test
    void getPermissionResponsesByRole() {
        PermissionResponse permissionResponse = new PermissionResponse("endpoint", true);
        when(permissionRepo.findAllByRoleName(anyString())).thenReturn(List.of(permission));
        when(permissionMapper.permissionListToPermissionResponseList(anyList()))
                .thenReturn(List.of(permissionResponse));

        List<PermissionResponse> permissionResponses = roleService.getPermissionResponsesByRole("ROLE_ACCOUNTANT");
        assertThat(permissionResponses).hasSize(1);
        assertThat(permissionResponses.get(0)).usingRecursiveComparison().isEqualTo(permissionResponse);

        verify(permissionRepo, times(1)).findAllByRoleName(anyString());
        verify(permissionMapper, times(1))
                .permissionListToPermissionResponseList(anyList());

        verifyNoMoreInteractions(permissionRepo);
        verifyNoMoreInteractions(permissionMapper);
    }

    @Test
    void updatePermissions() {
        when(permissionRepo.findAllByRoleName(anyString())).thenReturn(List.of(permission));
        when(permissionRepo.saveAll(anyIterable())).thenReturn(List.of(new Permission()));

        roleService.updatePermissions(new boolean[]{true}, new boolean[]{false},
                new boolean[]{true}, new boolean[]{false});

        verify(permissionRepo, times(4)).findAllByRoleName(anyString());
        verify(permissionRepo, times(4)).saveAll(anyIterable());

        verifyNoMoreInteractions(permissionRepo);
    }

    @Test
    void getAllowedEndPoint() {
        when(permissionRepo.findByStaffEmailThatAllowed(anyString())).thenReturn(List.of(permission));

        String endpoint = roleService.getAllowedEndPoint("email");
        assertThat(endpoint).isEqualTo(permission.getEndpoint().getEndpoint());

        verify(permissionRepo, times(1)).findByStaffEmailThatAllowed(anyString());
        verifyNoMoreInteractions(permissionRepo);
    }

    @Test
    void createPermissions_Table_Is_Empty() {
        Endpoint endpoint = new Endpoint();
        endpoint.setId(14L);
        Role role = new Role();
        role.setId(6L);
        Role role1 = new Role();
        role1.setId(2L);

        when(permissionRepo.count()).thenReturn(0L);
        when(endpointRepo.findAll((Sort) any())).thenReturn(List.of(endpoint, endpoint));
        when(roleRepo.findAll((Sort)any())).thenReturn(List.of(role, role1));
        when(permissionMapper.createPermission(any(Role.class), any(Endpoint.class), anyBoolean()))
                .thenReturn(new Permission());
        when(permissionRepo.saveAll(anyIterable())).thenReturn(List.of(new Permission()));

        roleService.createPermissions();

        verify(permissionRepo, times(1)).count();
        verify(endpointRepo, times(1)).findAll((Sort) any());
        verify(roleRepo, times(1)).findAll((Sort)any());
        verify(permissionMapper, times(4))
                .createPermission(any(Role.class), any(Endpoint.class), anyBoolean());
        verify(permissionRepo, times(1)).saveAll(anyIterable());

        verifyNoMoreInteractions(permissionRepo);
        verifyNoMoreInteractions(endpointRepo);
        verifyNoMoreInteractions(roleRepo);
        verifyNoMoreInteractions(permissionMapper);
    }

    @Test
    void createPermissions_Table_Is_Not_Empty() {
        when(permissionRepo.count()).thenReturn(1L);

        roleService.createPermissions();

        verify(permissionRepo, times(1)).count();
        verifyNoMoreInteractions(permissionRepo);
    }

}