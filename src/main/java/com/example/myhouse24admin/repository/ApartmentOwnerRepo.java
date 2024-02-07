package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.ApartmentOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ApartmentOwnerRepo extends JpaRepository<ApartmentOwner,Long>, JpaSpecificationExecutor<ApartmentOwner> {
    boolean existsApartmentOwnerByEmail(String email);
    boolean existsApartmentOwnerByPhoneNumber(String phoneNumber);
    boolean existsApartmentOwnerByViberNumber(String viberNumber);
    boolean existsApartmentOwnerByTelegramUsername(String telegramUsername);
}
