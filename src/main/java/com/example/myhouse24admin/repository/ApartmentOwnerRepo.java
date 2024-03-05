package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ApartmentOwnerRepo extends JpaRepository<ApartmentOwner, Long>, JpaSpecificationExecutor<ApartmentOwner> {
    Optional<ApartmentOwner> findByEmailAndDeletedIsFalse(String email);

    Optional<ApartmentOwner> findByPhoneNumberAndDeletedIsFalse(String phoneNumber);

    Optional<ApartmentOwner> findByViberNumberAndDeletedIsFalse(String viberNumber);

    Optional<ApartmentOwner> findByTelegramUsernameAndDeletedIsFalse(String telegramUsername);

    @Query(value = "SELECT * FROM apartment_owners WHERE deleted = false ORDER BY id DESC LIMIT 1", nativeQuery = true)
    ApartmentOwner findLast();

    List<ApartmentOwner> findApartmentOwnersByMessagesIn(Collection<Long> messages);
}
