package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.PaymentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentItemRepo extends JpaRepository<PaymentItem, Long> {
}
