package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.mapper.PersonalAccountMapper;
import com.example.myhouse24admin.model.personalAccounts.*;
import com.example.myhouse24admin.repository.PersonalAccountRepo;
import com.example.myhouse24admin.specification.PersonalAccountSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonalAccountServiceImplTest {

    @Mock
    private PersonalAccountRepo accountRepo;
    @Mock
    private PersonalAccountMapper accountMapper;
    @InjectMocks
    private PersonalAccountServiceImpl accountService;

    private static List<PersonalAccount> personalAccounts;

    @BeforeEach
    void setUp() {
        personalAccounts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PersonalAccount account = new PersonalAccount();
            account.setId((long) i);
            account.setAccountNumber(String.valueOf(i + 1));
            account.setStatus(PersonalAccountStatus.ACTIVE);

            Apartment apartment = new Apartment();
            apartment.setId((long) i);
            apartment.setPersonalAccount(account);
            apartment.setApartmentNumber("0000" + i);

            account.setApartment(apartment);
            personalAccounts.add(account);
        }
    }

    @Test
    void getAccountsFindByNumber() {
        // given
        List<PersonalAccountShortResponse> personalAccountShortResponses = new ArrayList<>();
        for (PersonalAccount account : personalAccounts) {
            PersonalAccountShortResponse response = new PersonalAccountShortResponse();
            response.setId(account.getId());
            response.setAccountNumber(account.getAccountNumber());
            personalAccountShortResponses.add(response);
        }
        Page<PersonalAccount> pagePersonalAccount = new PageImpl<>(personalAccounts, Pageable.ofSize(5), 5);

        // when
        when(accountRepo.findAll(any(PersonalAccountSpecification.class), any(Pageable.class)))
                .thenReturn(pagePersonalAccount);
        when(accountMapper.personalAccountListToPersonalAccountShortResponseList(anyList()))
                .thenReturn(personalAccountShortResponses);

        // call method
        Page<PersonalAccountShortResponse> foundAccounts = accountService.getAccountsFindByNumber(0, 5, "1");

        // then
        assertEquals(5, foundAccounts.getNumberOfElements());
        assertFalse(foundAccounts.getContent().isEmpty());

        verify(accountRepo, times(1)).findAll(any(PersonalAccountSpecification.class), any(Pageable.class));
        verify(accountMapper, times(1)).personalAccountListToPersonalAccountShortResponseList(anyList());
    }

    @Test
    void getPersonalAccounts() {
        // given
        List<PersonalAccountTableResponse> personalAccountTableResponses = new ArrayList<>();
        for (PersonalAccount account : personalAccounts) {
            PersonalAccountTableResponse response = new PersonalAccountTableResponse();
            response.setId(account.getId());
            response.setAccountNumber(account.getAccountNumber());
            response.setStatus(account.getStatus());
            personalAccountTableResponses.add(response);
        }
        PageImpl<PersonalAccount> personalAccountPage = new PageImpl<>(personalAccounts, Pageable.ofSize(5), 5);
        Map<String, String> searchParams = Map.of("accountNumber", "1");

        // when
        when(accountRepo.findAll(any(PersonalAccountSpecification.class), any(Pageable.class)))
                .thenReturn(personalAccountPage);
        when(accountMapper.personalAccountListToPersonalAccountTableResponseList(anyList()))
                .thenReturn(personalAccountTableResponses);

        // call method
        Page<PersonalAccountTableResponse> personalAccountsResult = accountService.getPersonalAccounts(0, 5, searchParams);
        List<PersonalAccountTableResponse> content = personalAccountsResult.getContent();

        // then
        assertFalse(content.isEmpty());
        assertEquals(5, content.size());
        assertEquals(5, personalAccountsResult.getNumberOfElements());

        verify(accountRepo, times(1)).findAll(any(PersonalAccountSpecification.class), any(Pageable.class));
        verify(accountMapper, times(1)).personalAccountListToPersonalAccountTableResponseList(anyList());
    }

    @Test
    void getPersonalAccountStatuses() {
        // call method
        List<PersonalAccountStatus> statuses = accountService.getPersonalAccountStatuses();

        // then
        assertFalse(statuses.isEmpty());
        assertEquals(2, statuses.size());
    }

    @Test
    void addNewPersonalAccount_ApartmentHasDifferentPersonalAccount() {
        // given
        PersonalAccountAddRequest request = new PersonalAccountAddRequest();
        request.setApartmentId(1L);
        request.setHouseId(1L);
        request.setSectionId(1L);
        request.setAccountNumber("1L");
        request.setStatus(PersonalAccountStatus.ACTIVE);

        // when
        when(accountRepo.existsPersonalAccountByApartment_Id((eq(request.getApartmentId()))))
                .thenReturn(true);
        when(accountRepo.findPersonalAccountByApartment_Id((eq(request.getApartmentId()))))
                .thenReturn(Optional.of(personalAccounts.get(0)));
        when(accountMapper.personalAccountAddRequestToPersonalAccount(eq(request)))
                .thenReturn(personalAccounts.get(0));
        when(accountRepo.save(any(PersonalAccount.class)))
                .thenReturn(personalAccounts.get(0));

        // call method
        accountService.addNewPersonalAccount(request);

        // then
        verify(accountRepo, times(1)).existsPersonalAccountByApartment_Id((eq(request.getApartmentId())));
        verify(accountRepo, times(1)).findPersonalAccountByApartment_Id((eq(request.getApartmentId())));
        verify(accountRepo, times(2)).save(any(PersonalAccount.class));
    }

    @Test
    void addNewPersonalAccount_ApartmentHasDifferentPersonalAccount_AndPersonalAccountByApartmentIdNotFound() {
        // given
        PersonalAccountAddRequest request = new PersonalAccountAddRequest();
        request.setApartmentId(1L);
        request.setHouseId(1L);
        request.setSectionId(1L);
        request.setAccountNumber("1L");
        request.setStatus(PersonalAccountStatus.ACTIVE);

        // when
        when(accountRepo.existsPersonalAccountByApartment_Id((eq(request.getApartmentId()))))
                .thenReturn(true);
        when(accountRepo.findPersonalAccountByApartment_Id((eq(request.getApartmentId()))))
                .thenReturn(Optional.empty());

        // call method
        assertThrows(EntityNotFoundException.class, () -> accountService.addNewPersonalAccount(request));

        // then
        verify(accountRepo, times(1)).existsPersonalAccountByApartment_Id((eq(request.getApartmentId())));
        verify(accountRepo, times(1)).findPersonalAccountByApartment_Id((eq(request.getApartmentId())));
        verify(accountRepo, never()).save(any(PersonalAccount.class));
    }

    @Test
    void getPersonalAccountById() {
        // given
        PersonalAccount account = personalAccounts.get(0);
        PersonalAccountResponse response = new PersonalAccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setStatus(account.getStatus());

        // when
        when(accountRepo.findById((eq(account.getId()))))
                .thenReturn(Optional.of(account));
        when(accountMapper.personalAccountToPersonalAccountResponse(eq(account)))
                .thenReturn(response);

        // call method
        PersonalAccountResponse personalAccountById = accountService.getPersonalAccountById(account.getId());

        // then
        assertNotNull(personalAccountById);
        assertEquals(account.getId(), personalAccountById.getId());
        assertEquals(account.getAccountNumber(), personalAccountById.getAccountNumber());
        assertEquals(account.getStatus(), personalAccountById.getStatus());

        verify(accountRepo, times(1)).findById((eq(account.getId())));
        verify(accountMapper, times(1)).personalAccountToPersonalAccountResponse(eq(account));
    }


    @Test
    void updatePersonalAccount() {
        // given
        PersonalAccount account = personalAccounts.get(0);
        PersonalAccountUpdateRequest request = new PersonalAccountUpdateRequest();
        request.setId(1L);
        request.setApartmentId(1L);
        request.setHouseId(1L);
        request.setSectionId(1L);
        request.setAccountNumber("1L");
        request.setStatus(PersonalAccountStatus.ACTIVE);

        // when
        when(accountRepo.findById((eq(request.getId()))))
                .thenReturn(Optional.of(account));
        when(accountRepo.existsPersonalAccountByApartment_IdAndIdIsNot((eq(request.getApartmentId())), (eq(request.getId()))))
                .thenReturn(true);
        when(accountRepo.findPersonalAccountByApartment_Id((eq(request.getApartmentId()))))
                .thenReturn(Optional.of(account));

        // call method
        accountService.updatePersonalAccount(request);

        // then
        verify(accountRepo, times(1)).findById((eq(request.getId())));
        verify(accountRepo, times(1)).existsPersonalAccountByApartment_IdAndIdIsNot((eq(request.getApartmentId())), (eq(request.getId())));
        verify(accountRepo, times(1)).findPersonalAccountByApartment_Id((eq(request.getApartmentId())));
        verify(accountRepo, times(2)).save(any(PersonalAccount.class));

    }

    @Test
    void updatePersonalAccount_WhenPersonalAccountByIdNotFound() {
        // given
        PersonalAccountUpdateRequest request = new PersonalAccountUpdateRequest();
        request.setId(1L);

        // when
        when(accountRepo.findById((eq(request.getId()))))
                .thenReturn(Optional.empty());

        // call method
        assertThrows(EntityNotFoundException.class, () -> accountService.updatePersonalAccount(request));
        verify(accountRepo, times(1)).findById((eq(request.getId())));
    }

    @Test
    void getMinimalFreeAccountNumber() {
        // when
        when(accountRepo.findMinimalFreeAccountNumber())
                .thenReturn("1");

        // call method
        String minimalFreeAccountNumber = accountService.getMinimalFreeAccountNumber();

        // then
        assertNotNull(minimalFreeAccountNumber);
        assertEquals("00000-00001", minimalFreeAccountNumber);
        verify(accountRepo, times(1)).findMinimalFreeAccountNumber();
    }

    @Test
    void exportToExcel() {
        // given
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("apartmentId", "1");
        List<PersonalAccountTableResponse> personalAccountTableResponses = new ArrayList<>();
        PersonalAccountTableResponse personalAccountTableResponse = new PersonalAccountTableResponse();
        personalAccountTableResponse.setId(1L);
        personalAccountTableResponse.setAccountNumber("1L");
        personalAccountTableResponse.setStatus(PersonalAccountStatus.ACTIVE);
        personalAccountTableResponses.add(personalAccountTableResponse);

        // when
        when(accountRepo.findAll(any(PersonalAccountSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(personalAccounts));
        when(accountMapper.personalAccountListToPersonalAccountTableResponseList(anyList()))
                .thenReturn(personalAccountTableResponses);

        // call method
        List<PersonalAccountTableResponse> responseList = accountService.exportToExcel(1, 10, searchParams);

        // then
        assertFalse(responseList.isEmpty());
        assertEquals(personalAccountTableResponses.size(), responseList.size());
        verify(accountRepo, times(1)).findAll(any(PersonalAccountSpecification.class), any(Pageable.class));
        verify(accountMapper, times(1)).personalAccountListToPersonalAccountTableResponseList(anyList());
    }
}