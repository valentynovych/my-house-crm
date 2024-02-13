package com.example.myhouse24admin.repository;

import com.example.myhouse24admin.entity.PersonalAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalAccountRepo extends JpaRepository<PersonalAccount, Long>,
        JpaSpecificationExecutor<PersonalAccount> {

    boolean existsPersonalAccountByAccountNumber(Long accountNumber);

    @Query(value = "SELECT max(accountNumber) FROM PersonalAccount")
    Long getMaxAccountNumber();
}
