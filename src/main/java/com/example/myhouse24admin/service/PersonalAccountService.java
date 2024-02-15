package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.apartments.personaAccount.PersonalAccountShortResponse;
import org.springframework.data.domain.Page;

public interface PersonalAccountService {
    Page<PersonalAccountShortResponse> getAccountsFindByNumber(int page, int pageSize, String accountNumber);
}
