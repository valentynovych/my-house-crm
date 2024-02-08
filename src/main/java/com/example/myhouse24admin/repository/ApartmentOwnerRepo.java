package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.ApartmentOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ApartmentOwnerRepo extends JpaRepository<ApartmentOwner,Long>, JpaSpecificationExecutor<ApartmentOwner> {
    boolean existsApartmentOwnerByEmail(String email);
    boolean existsApartmentOwnerByPhoneNumber(String phoneNumber);
    boolean existsApartmentOwnerByViberNumber(String viberNumber);
    boolean existsApartmentOwnerByTelegramUsername(String telegramUsername);
    Optional<ApartmentOwner> findByEmail(String email);
    Optional<ApartmentOwner> findByPhoneNumber(String phoneNumber);
    Optional<ApartmentOwner> findByViberNumber(String viberNumber);
    Optional<ApartmentOwner> findByTelegramUsername(String telegramUsername);

}
