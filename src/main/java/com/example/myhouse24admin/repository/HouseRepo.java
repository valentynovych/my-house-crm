package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseRepo extends JpaRepository<House, Long>, JpaSpecificationExecutor<House> {
}
