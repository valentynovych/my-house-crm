package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.CashSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CashSheetRepo extends JpaRepository<CashSheet, Long>, JpaSpecificationExecutor<CashSheet> {
    @Query(value = "SELECT next_val FROM cash_sheets_seq", nativeQuery = true)
    Long getMaxId();

    Optional<CashSheet> findCashSheetByIdAndDeletedIsFalse(Long cashSheetId);

    boolean existsCashSheetByPaymentItem_Id(Long paymentItemId);

    @Async
    CompletableFuture<List<CashSheet>> findByCreationDateBetweenAndDeletedIsFalse(Instant dateFrom, Instant dateTo);

    Optional<CashSheet> findCashSheetByInvoice_Id(Long invoiceId);
}
