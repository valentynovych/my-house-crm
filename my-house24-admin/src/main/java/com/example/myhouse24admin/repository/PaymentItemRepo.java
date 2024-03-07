package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.PaymentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentItemRepo extends JpaRepository<PaymentItem, Long>, JpaSpecificationExecutor<PaymentItem> {

    Optional<PaymentItem> findByIdAndDeletedIsFalse(Long paymentItemId);
}
