package com.example.myhouse24rest.repository;

import com.example.myhouse24rest.entity.OwnerMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerMessageRepo extends JpaRepository<OwnerMessage, Long> {

    Optional<OwnerMessage> findOwnerMessageByMessageIdAndApartmentOwner_Email(Long messageId, String email);

    Page<OwnerMessage> findOwnerMessagesByApartmentOwner_EmailAndReadFalse(String name, int page, int pageSize, Pageable pageable);
}
