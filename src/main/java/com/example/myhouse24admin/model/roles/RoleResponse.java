package com.example.myhouse24admin.model.roles;

import java.util.List;

public record RoleResponse(
        List<Boolean> managerAllowances,
        List<Boolean> accountantAllowances,
        List<Boolean> electricianAllowances,
        List<Boolean> plumberAllowances
) {
}
