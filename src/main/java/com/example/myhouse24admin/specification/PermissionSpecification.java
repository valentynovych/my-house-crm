package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.Permission;
import com.example.myhouse24admin.entity.Role;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public interface PermissionSpecification {
    static Specification<Permission> byRoleId(Long roleId){
        return (root, query, builder) -> {
            Join<Permission, Role> roleJoin = root.join("role");
            return builder.equal(roleJoin.get("id"), roleId);
        };
    }
}
