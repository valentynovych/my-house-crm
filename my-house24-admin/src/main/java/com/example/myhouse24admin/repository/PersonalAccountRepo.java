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

    boolean existsPersonalAccountByAccountNumber(String accountNumber);

    boolean existsPersonalAccountByApartment_IdAndIdIsNot(Long apartmentId, Long personalAccountId);

    boolean existsPersonalAccountByApartment_Id(Long apartmentId);

    Optional<PersonalAccount> findPersonalAccountByAccountNumber(String accountNumber);

    Optional<PersonalAccount> findPersonalAccountByApartment_Id(Long apartmentId);

    @Query(value = "SELECT MIN(CONVERT(CONCAT(SUBSTRING(pa.account_number, 1, 5), SUBSTRING(pa.account_number, 7)), DECIMAL )) + 1 " +
            "FROM personal_accounts pa " +
            "LEFT JOIN personal_accounts addTable ON CONVERT(CONCAT(SUBSTRING(pa.account_number, 1, 5), SUBSTRING(pa.account_number, 7)), DECIMAL ) + 1 " +
            "                                            = CONVERT(CONCAT(SUBSTRING(addTable.account_number, 1, 5), SUBSTRING(addTable.account_number, 7)), DECIMAL) " +
            "WHERE addTable.account_number IS NULL", nativeQuery = true)
    String findMinimalFreeAccountNumber();

    int countPersonalAccountsByDeletedIsFalse();
}
