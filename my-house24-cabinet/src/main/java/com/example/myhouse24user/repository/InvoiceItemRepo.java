package com.example.myhouse24user.repository;

import com.example.myhouse24user.entity.Invoice;
import com.example.myhouse24user.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceItemRepo extends JpaRepository<InvoiceItem, Long>, JpaSpecificationExecutor<InvoiceItem> {
    @Query(value = "SELECT sum(cost) FROM invoice_items where invoice_id = :invoiceId", nativeQuery = true)
    BigDecimal getItemsSumByInvoiceId(@Param("invoiceId") Long invoiceId);

    List<InvoiceItem> findInvoiceItemsByInvoiceIn(List<Invoice> invoices);
}
