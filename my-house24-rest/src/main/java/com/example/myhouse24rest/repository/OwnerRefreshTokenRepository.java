package com.example.myhouse24rest.repository;

import com.example.myhouse24rest.entity.OwnerRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRefreshTokenRepository extends JpaRepository<OwnerRefreshToken, Long> {

    Optional<OwnerRefreshToken> findByRefreshTokenAndOwner_Email(String refreshToken, String email);

    Optional<OwnerRefreshToken> findByOwner_Email(String email);
}
