package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.CashSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CashSheetRepo extends JpaRepository<CashSheet, Long>, JpaSpecificationExecutor<CashSheet> {
    @Query(value = "SELECT next_val FROM cash_sheets_seq", nativeQuery = true)
    Long getMaxId();

    Optional<CashSheet> findCashSheetByIdAndDeletedIsFalse(Long cashSheetId);
}
