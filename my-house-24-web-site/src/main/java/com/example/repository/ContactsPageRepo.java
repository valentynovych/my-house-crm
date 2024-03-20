package com.example.repository;

import com.example.entity.ContactsPage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactsPageRepo extends JpaRepository<ContactsPage, Long> {
}
