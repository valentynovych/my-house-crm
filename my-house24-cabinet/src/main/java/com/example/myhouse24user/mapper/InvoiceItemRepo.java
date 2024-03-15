package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface InvoiceItemRepo extends JpaRepository<InvoiceItem, Long> {
    @Query(value = "SELECT sum(cost) FROM invoice_items where invoice_id = :invoiceId", nativeQuery = true)
    BigDecimal getItemsSumByInvoiceId(@Param("invoiceId") Long invoiceId);
}
