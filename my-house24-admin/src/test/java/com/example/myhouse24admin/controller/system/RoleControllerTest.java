package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.entity.OwnerStatus;
import com.example.myhouse24admin.model.roles.RoleResponse;
import com.example.myhouse24admin.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class RoleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails userDetails;

    @Autowired
    private RoleService roleService;

    @Test
    void getRolesPage() throws Exception {
        this.mockMvc.perform(get("/my-house/admin/system-settings/roles")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("system/roles/roles"));
    }

    @Test
    void getRoleResponse() throws Exception {
        RoleResponse roleResponse = new RoleResponse(List.of(true), List.of(false),
                List.of(true), List.of(false));

        when(roleService.createRoleResponse()).thenReturn(roleResponse);

        this.mockMvc.perform(get("/my-house/admin/system-settings/roles/getRoles")
                        .contextPath("/my-house")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managerAllowances[0]").value(roleResponse.managerAllowances().get(0)))
                .andExpect(jsonPath("$.electricianAllowances[0]").value(roleResponse.electricianAllowances().get(0)))
                .andExpect(jsonPath("$.plumberAllowances[0]").value(roleResponse.plumberAllowances().get(0)))
                .andExpect(jsonPath("$.accountantAllowances[0]").value(roleResponse.accountantAllowances().get(0)));

    }

    @Test
    void updateRoles() throws Exception {
        doNothing().when(roleService).updatePermissions(any(), any(), any(), any());

        this.mockMvc.perform(post("/my-house/admin/system-settings/roles/update")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("managerPermissions[]","true")
                        .param("accountantPermissions[]","true")
                        .param("electricianPermissions[]","true")
                        .param("plumberPermissions[]","true"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}