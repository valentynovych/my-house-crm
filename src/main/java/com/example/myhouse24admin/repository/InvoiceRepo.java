package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {
    @Query(value = "SELECT * FROM invoices WHERE deleted = false ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Invoice> findLast();
}
