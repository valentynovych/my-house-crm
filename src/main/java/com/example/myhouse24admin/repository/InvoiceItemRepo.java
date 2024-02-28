package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepo extends JpaRepository<InvoiceItem, Long> {
}
