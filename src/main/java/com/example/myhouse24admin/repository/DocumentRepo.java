package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepo extends JpaRepository<Document, Long> {
}
