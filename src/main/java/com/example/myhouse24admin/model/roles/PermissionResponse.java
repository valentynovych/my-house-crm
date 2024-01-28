package com.example.myhouse24admin.model.roles;

public record PermissionResponse(
        String endpoint,
        boolean allowed
) {
}
