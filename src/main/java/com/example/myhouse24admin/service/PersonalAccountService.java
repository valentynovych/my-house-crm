package com.example.myhouse24admin.service;

import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.model.personalAccounts.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface PersonalAccountService {
    Page<PersonalAccountShortResponse> getAccountsFindByNumber(int page, int pageSize, String accountNumber);

    Page<PersonalAccountTableResponse> getPersonalAccounts(int page, int pageSize, Map<String, String> searchParams);

    List<PersonalAccountStatus> getPersonalAccountStatuses();

    void addNewPersonalAccount(PersonalAccountAddRequest request);

    PersonalAccountResponse getPersonalAccountById(Long accountId);

    void updatePersonalAccount(PersonalAccountUpdateRequest request);
}
