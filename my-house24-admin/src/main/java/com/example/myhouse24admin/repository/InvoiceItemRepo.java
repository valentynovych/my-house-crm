package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface InvoiceItemRepo extends JpaRepository<InvoiceItem, Long>, JpaSpecificationExecutor<InvoiceItem> {
    @Query(value = "SELECT sum(cost) FROM invoice_items where invoice_id = :invoiceId", nativeQuery = true)
    BigDecimal getItemsSumByInvoiceId(@Param("invoiceId") Long invoiceId);

    @Async
    @Query(value = "SELECT sum(cost) FROM InvoiceItem where invoice.id in :invoiceIds")
    CompletableFuture<BigDecimal> getItemsSumByInvoiceIdIn(@Param("invoiceIds") List<Long> invoiceIds);

    boolean existsInvoiceItemByService_Id(Long serviceId);

    boolean existsInvoiceItemByService_UnitOfMeasurement_Id(Long unitId);
}
