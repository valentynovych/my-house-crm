package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.OwnerPasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerPasswordResetTokenRepo extends JpaRepository<OwnerPasswordResetToken, Long> {
}
