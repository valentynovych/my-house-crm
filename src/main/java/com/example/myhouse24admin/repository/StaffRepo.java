package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface StaffRepo extends JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {
    Optional<Staff> findByEmail(String email);

    boolean existsStaffByPhoneNumber(String phoneNumber);

    boolean existsStaffByEmail(String email);

    Optional<Staff> findByPhoneNumber(String phoneNumber);
}
