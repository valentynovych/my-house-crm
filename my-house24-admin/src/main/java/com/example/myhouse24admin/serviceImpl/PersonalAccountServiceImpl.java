package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.mapper.PersonalAccountMapper;
import com.example.myhouse24admin.model.personalAccounts.*;
import com.example.myhouse24admin.repository.PersonalAccountRepo;
import com.example.myhouse24admin.service.PersonalAccountService;
import com.example.myhouse24admin.specification.PersonalAccountSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        logger.info("getAccountsFindByNumber() -> start, with parameters: page: {}, pageSize: {}, accountNumber: {}",
                page, pageSize, accountNumber);
        Page<PersonalAccount> pagePersonalAccount =
                findPagePersonalAccount(page, pageSize,
                        Map.of("accountNumber", accountNumber,
                                "apartmentNull", "null"));
        List<PersonalAccountShortResponse> responseList =
                accountMapper.personalAccountListToPersonalAccountShortResponseList(pagePersonalAccount.getContent());
        Page<PersonalAccountShortResponse> responsePage =
                new PageImpl<>(responseList, pagePersonalAccount.getPageable(), pagePersonalAccount.getTotalElements());
        logger.info("findPagePersonalAccount() -> exit, return page PersonalAccountShortResponse with elem: {}",
                responsePage.getNumberOfElements());
        return responsePage;
    }

    @Override
    public Page<PersonalAccountTableResponse> getPersonalAccounts(int page,
                                                                  int pageSize,
                                                                  Map<String, String> searchParams) {
        logger.info("getPersonalAccounts() -> start, with parameters: page: {}, pageSize: {}, searchParams: {}",
                page, pageSize, searchParams);
        Page<PersonalAccount> pagePersonalAccount = findPagePersonalAccount(page, pageSize, searchParams);
        List<PersonalAccountTableResponse> responseList =
                accountMapper.personalAccountListToPersonalAccountTableResponseList(pagePersonalAccount.getContent());
        Page<PersonalAccountTableResponse> responsePage =
                new PageImpl<>(responseList, pagePersonalAccount.getPageable(), pagePersonalAccount.getTotalElements());
        logger.info("findPagePersonalAccount() -> exit, return page PersonalAccountTableResponse with elem: {}",
                responsePage.getNumberOfElements());
        return responsePage;
    }

    @Override
    public List<PersonalAccountStatus> getPersonalAccountStatuses() {
        logger.info("getPersonalAccountStatuses() -> start");
        return Arrays.stream(PersonalAccountStatus.values()).toList();
    }

    @Override
    public void addNewPersonalAccount(PersonalAccountAddRequest request) {
        logger.info("addNewPersonalAccount() -> start");
        if (request.getApartmentId() != null && accountRepo.existsPersonalAccountByApartment_Id(request.getApartmentId())) {
            Long apartmentId = request.getApartmentId();
            logger.info("addNewPersonalAccount() -> Apartment with id: {} has different PersonalAccount, " +
                    "start deleting Apartment in that Account", apartmentId);
            deleteApartmentOnPersonalAccount(apartmentId);
        }
        PersonalAccount personalAccount = accountMapper.personalAccountAddRequestToPersonalAccount(request);
        PersonalAccount save = accountRepo.save(personalAccount);
        logger.info("addNewPersonalAccount() -> exit, success saving new PersonalAccount with id: {}", save.getId());
    }

    @Override
    public PersonalAccountResponse getPersonalAccountById(Long accountId) {
        logger.info("getPersonalAccountById() -> start, with id: {}", accountId);
        PersonalAccount account = findPersonalAccountById(accountId);
        PersonalAccountResponse response = accountMapper.personalAccountToPersonalAccountResponse(account);
        logger.info("getPersonalAccountById() -> exit, return PersonalAccountResponse()");
        return response;
    }

    @Override
    public void updatePersonalAccount(PersonalAccountUpdateRequest request) {
        Long requestId = request.getId();
        logger.info("updatePersonalAccount() -> start with id: {}", requestId);
        PersonalAccount personalAccount = findPersonalAccountById(requestId);
        if (request.getApartmentId() != null
                && accountRepo.existsPersonalAccountByApartment_IdAndIdIsNot(request.getApartmentId(), request.getId())) {
            Long apartmentId = request.getApartmentId();
            logger.info("updatePersonalAccount() -> Apartment with id: {} has different PersonalAccount, " +
                    "start deleting Apartment in that Account", apartmentId);
            deleteApartmentOnPersonalAccount(apartmentId);
        }
        accountMapper.updatePersonalAccountFromRequest(personalAccount, request);
        accountRepo.save(personalAccount);
        logger.info("updatePersonalAccount() -> exit, success update PersonalAccount with id: {}", requestId);
    }

    @Override
    public String getMinimalFreeAccountNumber() {
        logger.info("getMinimalFreeAccountNumber() -> start");
        String minimalFreeAccountNumber = accountRepo.findMinimalFreeAccountNumber();
        String leftPad = StringUtils.leftPad(minimalFreeAccountNumber, 11, "00000-00000");
        logger.info("getMinimalFreeAccountNumber() -> exit, return minimalFreeAccountNumber: {}", leftPad);
        return leftPad;
    }

    @Override
    public List<PersonalAccountTableResponse> exportToExcel(int page, int pageSize, Map<String, String> searchParams) {
        logger.info("exportToExcel() -> start, with parameters: page: {}, pageSize: {}, searchParams: {}",
                page, pageSize, searchParams);
        Page<PersonalAccount> pagePersonalAccount = findPagePersonalAccount(page, pageSize, searchParams);
        List<PersonalAccountTableResponse> responseList =
                accountMapper.personalAccountListToPersonalAccountTableResponseList(pagePersonalAccount.getContent());
        logger.info("exportToExcel() -> exit, return List<PersonalAccountTableResponse> with size: {}", responseList.size());
        return responseList;
    }

    private PersonalAccount findPersonalAccountById(Long personalAccountId) {
        logger.info("findPersonalAccountById() -> start with id: {}", personalAccountId);
        Optional<PersonalAccount> byId = accountRepo.findById(personalAccountId);
        PersonalAccount personalAccount = byId.orElseThrow(() -> {
            logger.error("findPersonalAccountById() -> PersonalAccount with id: {} not found", personalAccountId);
            return new EntityNotFoundException(String.format("PersonalAccount with id: %s not found", personalAccountId));
        });
        logger.info("findPersonalAccountById() -> exit, PersonalAccount isPresent");
        return personalAccount;
    }

    private Page<PersonalAccount> findPagePersonalAccount(int page, int pageSize, Map<String, String> searchParams) {
        logger.info("findPagePersonalAccount() -> start, with parameters: page: {}, pageSize: {}, searchParams: {}",
                page, pageSize, searchParams);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "accountNumber"));
        PersonalAccountSpecification specification = new PersonalAccountSpecification(searchParams);
        Page<PersonalAccount> all = accountRepo.findAll(specification, pageable);
        logger.info("findPagePersonalAccount() -> exit, return page PersonalAccount with elem: {}", all.getNumberOfElements());
        return all;
    }

    private void deleteApartmentOnPersonalAccount(Long apartmentId) {
        logger.info("deleteApartmentOnPersonalAccount() -> start with id: {}", apartmentId);
        Optional<PersonalAccount> byId = accountRepo.findPersonalAccountByApartment_Id(apartmentId);
        PersonalAccount personalAccount = byId.orElseThrow(() -> {
            logger.error("deleteApartmentOnPersonalAccount() -> PersonalAccount with id: {} not found", apartmentId);
            return new EntityNotFoundException(String.format("PersonalAccount with id: %s not found", apartmentId));
        });
        personalAccount.setApartment(null);
        logger.info("deleteApartmentOnPersonalAccount() -> PersonalAccount with id: {} set Apartment - null",
                personalAccount.getId());
        accountRepo.save(personalAccount);
        logger.info("deleteApartmentOnPersonalAccount() -> exit, success delete Apartment in PersonalAccount " +
                "with id: {}", apartmentId);
    }
}
