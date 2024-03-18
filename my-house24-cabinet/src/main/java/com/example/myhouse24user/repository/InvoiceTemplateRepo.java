package com.example.myhouse24user.repository;

import com.example.myhouse24user.entity.InvoiceTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceTemplateRepo extends JpaRepository<InvoiceTemplate, Long>, JpaSpecificationExecutor<InvoiceTemplate> {
}
