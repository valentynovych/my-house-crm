package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.PersonalAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalAccountRepo extends JpaRepository<PersonalAccount, Long>,
        JpaSpecificationExecutor<PersonalAccount> {

    boolean existsPersonalAccountByAccountNumber(Long accountNumber);

    boolean existsPersonalAccountByApartment_IdAndIdIsNot(Long apartmentId, Long personalAccountId);

    Optional<PersonalAccount> findPersonalAccountByAccountNumber(Long accountNumber);

    @Query(value = "SELECT max(accountNumber) FROM PersonalAccount")
    Long getMaxAccountNumber();

    Optional<PersonalAccount> findPersonalAccountByApartment(Apartment apartment);
    Optional<PersonalAccount> findPersonalAccountByApartment_Id(Long apartmentId);
}
