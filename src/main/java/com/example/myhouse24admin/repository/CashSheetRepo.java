package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.CashSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CashSheetRepo extends JpaRepository<CashSheet, Long>, JpaSpecificationExecutor<CashSheet> {
}
