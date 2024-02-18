package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.mapper.PersonalAccountMapper;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountAddRequest;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountShortResponse;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountTableResponse;
import com.example.myhouse24admin.repository.PersonalAccountRepo;
import com.example.myhouse24admin.service.PersonalAccountService;
import com.example.myhouse24admin.specification.PersonalAccountSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PersonalAccountServiceImpl implements PersonalAccountService {

    private final PersonalAccountRepo accountRepo;
    private final PersonalAccountMapper accountMapper;
    private final Logger logger = LogManager.getLogger(PersonalAccountServiceImpl.class);

    public PersonalAccountServiceImpl(PersonalAccountRepo accountRepo, PersonalAccountMapper accountMapper) {
        this.accountRepo = accountRepo;
        this.accountMapper = accountMapper;
    }

    @Override
    public Page<PersonalAccountShortResponse> getAccountsFindByNumber(int page,
                                                                      int pageSize,
                                                                      String accountNumber) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "accountNumber"));
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("accountNumber", accountNumber);
        PersonalAccountSpecification specification = new PersonalAccountSpecification(searchParams);
        Page<PersonalAccount> all = accountRepo.findAll(specification, pageable);
        List<PersonalAccountShortResponse> responseList =
                accountMapper.personalAccountListToPersonalAccountShortResponseList(all.getContent());
        Page<PersonalAccountShortResponse> responsePage = new PageImpl<>(responseList, pageable, all.getTotalElements());
        return responsePage;
    }

    @Override
    public Page<PersonalAccountTableResponse> getPersonalAccounts(int page,
                                                                  int pageSize,
                                                                  Map<String, String> searchParams) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "accountNumber"));
        PersonalAccountSpecification specification = new PersonalAccountSpecification(searchParams);
        Page<PersonalAccount> all = accountRepo.findAll(specification, pageable);
        List<PersonalAccountTableResponse> responseList =
                accountMapper.personalAccountListToPersonalAccountTableResponseList(all.getContent());
        Page<PersonalAccountTableResponse> responsePage =
                new PageImpl<>(responseList, pageable, all.getTotalElements());
        return responsePage;
    }

    @Override
    public List<PersonalAccountStatus> getPersonalAccountStatuses() {
        return Arrays.stream(PersonalAccountStatus.values()).toList();
    }

    @Override
    public void addNewPersonalAccount(PersonalAccountAddRequest request) {
        logger.info("addNewPersonalAccount() -> start");
        PersonalAccount personalAccount = accountMapper.personalAccountAddRequestToPersonalAccount(request);
        PersonalAccount save = accountRepo.save(personalAccount);
        logger.info("addNewPersonalAccount() -> exit, success saving new PersonalAccount with id: {}", save.getId());
    }
}
