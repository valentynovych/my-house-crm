package com.example.myhouse24user.repository;


import com.example.myhouse24user.entity.ApartmentOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ApartmentOwnerRepo extends JpaRepository<ApartmentOwner, Long>, JpaSpecificationExecutor<ApartmentOwner> {
    Optional<ApartmentOwner> findByEmail(String email);
    boolean existsApartmentOwnerByEmail(String email);
}
