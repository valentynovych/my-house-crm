package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepo extends JpaRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {
}
