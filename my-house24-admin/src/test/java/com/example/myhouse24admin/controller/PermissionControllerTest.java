package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.roles.PermissionResponse;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class PermissionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetails userDetails;

    @Autowired
    private RoleService roleService;

    @Test
    void getRoleResponse() throws Exception {
        PermissionResponse permissionResponse = new PermissionResponse("endpoint",
                true);

        when(roleService.getPermissionResponsesByRole(anyString()))
                .thenReturn(List.of(permissionResponse));

        this.mockMvc.perform(get("/my-house/admin/getPermissions")
                        .contextPath("/my-house")
                        .with(user(userDetails))
                        .param("role", "ADMIN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].endpoint").value(permissionResponse.endpoint()))
                .andExpect(jsonPath("$.[0].allowed").value(permissionResponse.allowed()));
    }
}