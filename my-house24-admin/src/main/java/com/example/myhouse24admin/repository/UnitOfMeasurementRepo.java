package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.UnitOfMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitOfMeasurementRepo extends JpaRepository<UnitOfMeasurement, Long>,
        JpaSpecificationExecutor<UnitOfMeasurement> {

    List<UnitOfMeasurement> findAllByDeletedFalse();
}
