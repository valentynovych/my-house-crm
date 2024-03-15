package com.example.myhouse24user.repository;


import com.example.myhouse24user.entity.ApartmentOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ApartmentOwnerRepo extends JpaRepository<ApartmentOwner, Long>, JpaSpecificationExecutor<ApartmentOwner> {
    Optional<ApartmentOwner> findByEmail(String email);
    Optional<ApartmentOwner> findByEmailAndDeletedIsFalse(String email);
    @Query(value = "SELECT * FROM apartment_owners WHERE deleted = false ORDER BY id DESC LIMIT 1", nativeQuery = true)
    ApartmentOwner findLast();
    boolean existsApartmentOwnerByEmail(String email);
}
