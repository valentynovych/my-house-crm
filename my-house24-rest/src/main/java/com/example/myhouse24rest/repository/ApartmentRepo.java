package com.example.myhouse24rest.repository;

import com.example.myhouse24rest.entity.Apartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepo extends JpaRepository<Apartment, Long> {
    Page<Apartment> findAllByOwner_Email(String name, Pageable pageable);
}
