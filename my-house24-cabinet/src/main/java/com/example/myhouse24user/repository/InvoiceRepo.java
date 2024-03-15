package com.example.myhouse24user.repository;

import com.example.myhouse24user.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceRepo extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
}
