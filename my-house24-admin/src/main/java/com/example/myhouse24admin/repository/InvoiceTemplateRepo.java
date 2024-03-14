package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.InvoiceTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceTemplateRepo extends JpaRepository<InvoiceTemplate, Long>, JpaSpecificationExecutor<InvoiceTemplate> {
}
