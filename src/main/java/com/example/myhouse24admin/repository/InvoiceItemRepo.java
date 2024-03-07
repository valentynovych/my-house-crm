package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface InvoiceItemRepo extends JpaRepository<InvoiceItem, Long>, JpaSpecificationExecutor<InvoiceItem> {
    @Query(value = "SELECT sum(cost) FROM invoice_items where invoice_id = :invoiceId", nativeQuery = true)
    BigDecimal getItemsSumByInvoiceId(@Param("invoiceId")Long invoiceId);

    boolean existsInvoiceItemByService_Id(Long serviceId);
    boolean existsInvoiceItemByService_UnitOfMeasurement_Id(Long unitId);
}
