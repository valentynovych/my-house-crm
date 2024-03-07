package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailRepo extends JpaRepository<PaymentDetail, Long> {
}
