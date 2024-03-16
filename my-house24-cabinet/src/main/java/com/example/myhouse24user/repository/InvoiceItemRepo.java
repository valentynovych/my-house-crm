package com.example.myhouse24user.repository;

import com.example.myhouse24user.entity.Invoice;
import com.example.myhouse24user.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepo extends JpaRepository<InvoiceItem, Long>, JpaSpecificationExecutor<InvoiceItem> {

    List<InvoiceItem> findInvoiceItemsByInvoiceIn(List<Invoice> invoices);
}
