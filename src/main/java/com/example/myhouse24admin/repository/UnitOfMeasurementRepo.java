package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.UnitOfMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitOfMeasurementRepo extends JpaRepository<UnitOfMeasurement, Long>,
        JpaSpecificationExecutor<UnitOfMeasurement> {
}
