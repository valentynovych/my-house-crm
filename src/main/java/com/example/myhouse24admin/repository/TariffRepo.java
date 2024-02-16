package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.Tariff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepo extends JpaRepository<Tariff, Long>, JpaSpecificationExecutor<Tariff> {

    Page<Tariff> findAllByDeletedIsFalse(Pageable pageable);

}
