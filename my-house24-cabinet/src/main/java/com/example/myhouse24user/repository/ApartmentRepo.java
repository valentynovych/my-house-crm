package com.example.myhouse24user.repository;

import com.example.myhouse24user.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ApartmentRepo extends JpaRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {
}
