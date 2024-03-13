package com.example.myhouse24user.repository;

import com.example.myhouse24user.entity.OwnerPasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerPasswordResetTokenRepo extends JpaRepository<OwnerPasswordResetToken, Long> {
    Optional<OwnerPasswordResetToken> findByToken(String token);
}
