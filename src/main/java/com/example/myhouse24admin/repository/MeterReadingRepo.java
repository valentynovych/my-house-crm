package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MeterReadingRepo extends JpaRepository<MeterReading, Long> {
    @Query(value = "SELECT * FROM meter_readings WHERE deleted = false ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<MeterReading> findLast();
}
