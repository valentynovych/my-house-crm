package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepo extends JpaRepository<Staff, Long> {
    Optional<Staff> findByEmail(String email);
}
