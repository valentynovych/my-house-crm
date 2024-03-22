package com.example.myhouse24rest.repository;

import com.example.myhouse24rest.entity.ApartmentOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApartmentOwnerRepo extends JpaRepository<ApartmentOwner, Long> {

    Optional<ApartmentOwner> findByEmail(String email);
}
