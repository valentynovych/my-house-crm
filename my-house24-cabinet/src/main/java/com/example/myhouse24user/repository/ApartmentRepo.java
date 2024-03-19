package com.example.myhouse24user.repository;

import com.example.myhouse24user.entity.Apartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApartmentRepo extends JpaRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {

    Optional<Apartment> findApartmentByIdAndOwner_Email(Long apartmentId, String ownerEmail);

    Page<Apartment> findAllByOwner_Email(String ownerEmail, Pageable pageable);
}
