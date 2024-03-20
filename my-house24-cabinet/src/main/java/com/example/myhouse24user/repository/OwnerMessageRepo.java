package com.example.myhouse24user.repository;

import com.example.myhouse24user.entity.OwnerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnerMessageRepo extends JpaRepository<OwnerMessage, Long>, JpaSpecificationExecutor<OwnerMessage> {
    List<OwnerMessage> findAllByIdIsInAndApartmentOwner_Email(List<Long> ids, String email);

}
