package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PermissionRepo extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    @Query(value = "SELECT p FROM Permission p LEFT JOIN FETCH p.role r LEFT JOIN FETCH r.staff s LEFT JOIN FETCH p.endpoint e WHERE s.email = :email AND e.endpoint = :endpoint")
    Optional<Permission> findByStaffEmailAndEndpoint(String email, String endpoint);
    @Query(value = "SELECT p FROM Permission p LEFT JOIN FETCH p.role r LEFT JOIN FETCH r.staff s WHERE s.email = :email AND p.allowed = true")
    List<Permission> findByStaffEmailThatAllowed(String email);
}
