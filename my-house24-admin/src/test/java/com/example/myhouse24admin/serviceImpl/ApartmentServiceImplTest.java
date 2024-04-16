package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.mapper.ApartmentMapper;
import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.model.apartments.ApartmentExtendResponse;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import com.example.myhouse24admin.model.meterReadings.ApartmentNumberResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.PersonalAccountRepo;
import com.example.myhouse24admin.specification.ApartmentSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceImplTest {

    @Mock
    private ApartmentRepo apartmentRepo;
    @Mock
    private ApartmentMapper apartmentMapper;
    @Mock
    private PersonalAccountRepo personalAccountRepo;
    @InjectMocks
    private ApartmentServiceImpl apartmentService;
    private static Apartment apartment;

    @BeforeEach
    void setUp() {
        apartment = new Apartment();
        apartment.setId(1L);
        apartment.setApartmentNumber("00001");
        apartment.setOwner(new ApartmentOwner());
        apartment.setPersonalAccount(new PersonalAccount());
        apartment.setBalance(BigDecimal.ZERO);
        apartment.setArea(54.54);
        apartment.setFloor(new Floor());
        apartment.setSection(new Section());
        apartment.setHouse(new House());
        apartment.setTariff(new Tariff());
    }

    @Test
    void addNewApartment_WhenPersonalAccountIsNotNull_AndPersonalAccountIsNotExist() {
        // given
        ApartmentAddRequest apartmentAddRequest = new ApartmentAddRequest();
        apartmentAddRequest.setApartmentNumber(apartment.getApartmentNumber());
        apartmentAddRequest.setArea(apartment.getArea());
        apartmentAddRequest.setPersonalAccountId(1L);

        PersonalAccount personalAccount = new PersonalAccount();
        personalAccount.setId(1L);
        personalAccount.setApartment(apartment);
        personalAccount.setAccountNumber(1L);
        personalAccount.setStatus(PersonalAccountStatus.ACTIVE);
        personalAccount.setDeleted(false);
        apartment.setPersonalAccount(personalAccount);

        // when
        when(apartmentMapper.apartmentAddRequestToApartment(apartmentAddRequest))
                .thenReturn(apartment);
        when(personalAccountRepo.findById(1L))
                .thenReturn(Optional.empty());

        // call the method
        assertThrows(EntityNotFoundException.class, () -> apartmentService.addNewApartment(apartmentAddRequest));
    }

    @Test
    void addNewApartment_WhenPersonalAccountIsNotNull_WithNewPersonalAccount() {
        // given
        ApartmentAddRequest apartmentAddRequest = new ApartmentAddRequest();
        apartmentAddRequest.setApartmentNumber(apartment.getApartmentNumber());
        apartmentAddRequest.setArea(apartment.getArea());
        apartmentAddRequest.setPersonalAccountNew(1L);

        PersonalAccount personalAccount = new PersonalAccount();
        personalAccount.setId(1L);
        personalAccount.setApartment(apartment);
        personalAccount.setAccountNumber(1L);
        personalAccount.setStatus(PersonalAccountStatus.ACTIVE);
        personalAccount.setDeleted(false);
        apartment.setPersonalAccount(personalAccount);

        ArgumentCaptor<Apartment> apartmentArgumentCaptor = ArgumentCaptor.forClass(Apartment.class);

        // when
        when(apartmentMapper.apartmentAddRequestToApartment(apartmentAddRequest))
                .thenReturn(apartment);
        when(apartmentRepo.save(apartment))
                .thenReturn(apartment);

        // call the method
        apartmentService.addNewApartment(apartmentAddRequest);

        // then
        verify(apartmentMapper, times(1)).apartmentAddRequestToApartment(apartmentAddRequest);
        verify(apartmentRepo, times(1)).save(apartmentArgumentCaptor.capture());

        Apartment capturedApartment = apartmentArgumentCaptor.getValue();
        assertNotNull(capturedApartment.getPersonalAccount());
        assertNotNull(capturedApartment.getPersonalAccount().getApartment());
    }

    @Test
    void addNewApartment_WhenPersonalAccountIsNotNull_WithoutPersonalAccount() {
        // given
        ApartmentAddRequest apartmentAddRequest = new ApartmentAddRequest();
        apartmentAddRequest.setApartmentNumber(apartment.getApartmentNumber());
        apartmentAddRequest.setArea(apartment.getArea());

        PersonalAccount personalAccount = new PersonalAccount();
        personalAccount.setId(1L);
        personalAccount.setApartment(apartment);
        personalAccount.setAccountNumber(1L);
        personalAccount.setStatus(PersonalAccountStatus.ACTIVE);
        personalAccount.setDeleted(false);
        apartment.setPersonalAccount(personalAccount);
        ArgumentCaptor<Apartment> apartmentArgumentCaptor = ArgumentCaptor.forClass(Apartment.class);

        // when
        when(apartmentMapper.apartmentAddRequestToApartment(apartmentAddRequest))
                .thenReturn(apartment);
        when(apartmentRepo.save(apartment))
                .thenReturn(apartment);
        when(personalAccountRepo.findMinimalFreeAccountNumber())
                .thenReturn(1L);

        // call the method
        apartmentService.addNewApartment(apartmentAddRequest);

        // then
        verify(apartmentMapper, times(1)).apartmentAddRequestToApartment(apartmentAddRequest);
        verify(apartmentRepo, times(1)).save(apartmentArgumentCaptor.capture());
        verify(personalAccountRepo, times(1)).findMinimalFreeAccountNumber();

        Apartment capturedApartment = apartmentArgumentCaptor.getValue();
        assertNotNull(capturedApartment.getPersonalAccount());
        assertNotNull(capturedApartment.getPersonalAccount().getApartment());
    }

    @Test
    void addNewApartment_WhenPersonalAccountIsNotNull_AndPersonalAccountExist() {
        // given
        ApartmentAddRequest apartmentAddRequest = new ApartmentAddRequest();
        apartmentAddRequest.setApartmentNumber(apartment.getApartmentNumber());
        apartmentAddRequest.setArea(apartment.getArea());
        apartmentAddRequest.setPersonalAccountId(1L);

        PersonalAccount personalAccount = new PersonalAccount();
        personalAccount.setId(1L);
        personalAccount.setApartment(apartment);
        personalAccount.setAccountNumber(1L);
        personalAccount.setStatus(PersonalAccountStatus.ACTIVE);
        personalAccount.setDeleted(false);
        apartment.setPersonalAccount(personalAccount);
        ArgumentCaptor<PersonalAccount> personalAccountArgumentCaptor = ArgumentCaptor.forClass(PersonalAccount.class);
        ArgumentCaptor<Apartment> apartmentArgumentCaptor = ArgumentCaptor.forClass(Apartment.class);

        PersonalAccount personalAccount2 = new PersonalAccount();
        personalAccount2.setId(2L);

        // when
        when(apartmentMapper.apartmentAddRequestToApartment(apartmentAddRequest))
                .thenReturn(apartment);
        when(personalAccountRepo.findById(1L))
                .thenReturn(Optional.of(personalAccount2));
        when(apartmentRepo.save(apartment))
                .thenReturn(apartment);

        // call the method
        apartmentService.addNewApartment(apartmentAddRequest);

        // then
        verify(apartmentMapper, times(1)).apartmentAddRequestToApartment(apartmentAddRequest);
        verify(personalAccountRepo, times(1)).findById(1L);
        verify(personalAccountRepo, times(1)).save(personalAccountArgumentCaptor.capture());
        verify(apartmentRepo, times(1)).save(apartmentArgumentCaptor.capture());

        PersonalAccount capturedPersonalAccount = personalAccountArgumentCaptor.getValue();
        assertNull(capturedPersonalAccount.getApartment());

        Apartment capturedApartment = apartmentArgumentCaptor.getValue();
        assertNotNull(capturedApartment.getPersonalAccount());
        assertNotNull(capturedApartment.getPersonalAccount().getApartment());
    }

    @Test
    void getApartments() {
        // given
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("page", "0");
        searchParams.put("pageSize", "10");
        List<Apartment> apartments = List.of(apartment, apartment, apartment);
        List<ApartmentResponse> apartmentResponses = List.of(new ApartmentResponse(), new ApartmentResponse(),
                new ApartmentResponse());
        // when
        when(apartmentRepo.findAll(any(ApartmentSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(apartments, PageRequest.of(0, 10), 3));
        when(apartmentMapper.apartmentListToApartmentResponseList(apartments))
                .thenReturn(apartmentResponses);

        // call the method
        Page<ApartmentResponse> apartmentResponsesResult = apartmentService.getApartments(0, 10, searchParams);

        // then
        verify(apartmentRepo, times(1)).findAll(any(ApartmentSpecification.class), any(Pageable.class));
        verify(apartmentMapper, times(1)).apartmentListToApartmentResponseList(apartments);

        assertEquals(apartmentResponses, apartmentResponsesResult.getContent());
        assertEquals(3, apartmentResponsesResult.getTotalElements());
    }

    @Test
    void getApartmentById() {
        // given
        Long apartmentId = apartment.getId();
        ApartmentExtendResponse apartmentResponse = new ApartmentExtendResponse();

        // when
        when(apartmentRepo.findById(apartmentId))
                .thenReturn(Optional.of(apartment));
        when(apartmentMapper.apartmentToApartmentExtendResponse(apartment))
                .thenReturn(apartmentResponse);

        // call the method
        ApartmentExtendResponse apartmentResponseResult = apartmentService.getApartmentById(apartmentId);

        // then
        verify(apartmentRepo, times(1)).findById(apartmentId);
        verify(apartmentMapper, times(1)).apartmentToApartmentExtendResponse(apartment);

        assertEquals(apartmentResponse, apartmentResponseResult);
    }

    @Test
    void getApartmentById_WhenApartmentNotExist() {
        // given
        Long apartmentId = apartment.getId();

        // when
        when(apartmentRepo.findById(apartmentId))
                .thenReturn(Optional.empty());

        // call the method
        assertThrows(EntityNotFoundException.class, () -> apartmentService.getApartmentById(apartmentId));

        // then
        verify(apartmentRepo, times(1)).findById(apartmentId);
    }

    @Test
    void updateApartment_WhenRequestIdIsNull() {
        // given
        Long apartmentId = apartment.getId();
        ApartmentAddRequest apartmentRequest = new ApartmentAddRequest();

        // call the method
        assertThrows(IllegalStateException.class, () -> apartmentService.updateApartment(apartmentId, apartmentRequest));
    }

    @Test
    void updateApartment_WhenRequestIdIsNotNull() {
        // given
        Long apartmentId = apartment.getId();
        ApartmentAddRequest apartmentRequest = new ApartmentAddRequest();
        apartmentRequest.setId(1L);
        apartmentRequest.setPersonalAccountId(1L);

        PersonalAccount personalAccount = new PersonalAccount();
        personalAccount.setId(1L);
        apartment.setPersonalAccount(personalAccount);
        personalAccount.setApartment(apartment);

        // when
        when(apartmentRepo.findById(apartmentId))
                .thenReturn(Optional.of(apartment));
        doNothing().when(apartmentMapper).updateApartmentFromApartmentRequest(apartment, apartmentRequest);
        when(personalAccountRepo.findById(1L))
                .thenReturn(Optional.of(personalAccount));

        // call the method
        apartmentService.updateApartment(apartmentId, apartmentRequest);

        // then
        verify(apartmentRepo, times(1)).findById(apartmentId);
        verify(apartmentMapper, times(1)).updateApartmentFromApartmentRequest(apartment, apartmentRequest);
        verify(personalAccountRepo, times(1)).findById(1L);
        verify(apartmentRepo, times(1)).save(apartment);
    }

    @Test
    void updateApartment_WhenPersonalAccountNumberIsNew() {
        // given
        Long apartmentId = apartment.getId();
        ApartmentAddRequest apartmentRequest = new ApartmentAddRequest();
        apartmentRequest.setId(1L);
        apartmentRequest.setPersonalAccountNew(1L);

        PersonalAccount personalAccount = new PersonalAccount();
        personalAccount.setId(1L);
        apartment.setPersonalAccount(personalAccount);

        // when
        when(apartmentRepo.findById(apartmentId))
                .thenReturn(Optional.of(apartment));
        doNothing().when(apartmentMapper).updateApartmentFromApartmentRequest(apartment, apartmentRequest);

        // call the method
        apartmentService.updateApartment(apartmentId, apartmentRequest);

        // then
        verify(apartmentRepo, times(1)).findById(apartmentId);
        verify(apartmentMapper, times(1)).updateApartmentFromApartmentRequest(apartment, apartmentRequest);
        verify(apartmentRepo, times(1)).save(apartment);
    }

    @Test
    void getApartmentsForSelect() {
        // given
        Long houseId = 1L;
        Long sectorId = 1L;
        SelectSearchRequest selectSearchRequest = new SelectSearchRequest("search", 1);
        List<Apartment> apartments = List.of(apartment, apartment, apartment);
        Page<Apartment> apartmentPage = new PageImpl<>(apartments, PageRequest.of(0, 10), 3);
        List<ApartmentNumberResponse> apartmentNumberResponseList = List.of(
                new ApartmentNumberResponse(1L, "1"),
                new ApartmentNumberResponse(2L, "2"),
                new ApartmentNumberResponse(3L, "3"));

        // when
        when(apartmentRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(apartmentPage);
        when(apartmentMapper.apartmentListToApartmentNameResponse(apartments))
                .thenReturn(apartmentNumberResponseList);

        // call the method
        Page<ApartmentNumberResponse> apartmentForSelectResponses = apartmentService
                .getApartmentsForSelect(selectSearchRequest, houseId, sectorId);

        // then
        assertEquals(apartmentNumberResponseList, apartmentForSelectResponses.getContent());
        assertEquals(3, apartmentForSelectResponses.getTotalElements());

        verify(apartmentRepo, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(apartmentMapper, times(1)).apartmentListToApartmentNameResponse(apartments);
    }

    @Test
    void getAllApartmentsBy() {
        // given
        Pageable pageable = PageRequest.of(1, 5);
        ApartmentSpecification specification = new ApartmentSpecification(Map.of("houseId", "1"));
        List<Apartment> apartments = List.of(apartment, apartment, apartment);
        Page<Apartment> apartmentPage = new PageImpl<>(apartments, pageable, 12);

        // when
        when(apartmentRepo.findAll(any(ApartmentSpecification.class), any(Pageable.class)))
                .thenAnswer(new Answer<Page<Apartment>>() {
                    private int invocationCount = 0;
                    @Override
                    public Page<Apartment> answer(InvocationOnMock invocation) throws Throwable {

                        if (invocationCount == 0) {
                            invocationCount++;
                            return apartmentPage;
                        }
                        if (invocationCount == 1) {
                            invocationCount++;
                            return new PageImpl<>(new ArrayList<>(), pageable, 0);
                        }
                        return apartmentPage;
                    }
                });

        // call the method
        List<Apartment> allApartmentsBy = apartmentService.getAllApartmentsBy(pageable, new ArrayList<>(), specification);

        // then
        assertEquals(3, allApartmentsBy.size());

        verify(apartmentRepo, times(2)).findAll(any(ApartmentSpecification.class), any(Pageable.class));
    }

    @Test
    void deleteApartment() {
        // given
        Long apartmentId = 1L;
        ArgumentCaptor<Apartment> argumentCaptor = ArgumentCaptor.forClass(Apartment.class);

        // when
        when(apartmentRepo.findById(apartmentId)).thenReturn(Optional.of(apartment));

        // call the method
        apartmentService.deleteApartment(apartmentId);

        // then
        verify(apartmentRepo, times(1)).findById(apartmentId);
        verify(apartmentRepo, times(1)).save(argumentCaptor.capture());
        verify(personalAccountRepo, times(1)).save(any(PersonalAccount.class));

        Apartment savedApartment = argumentCaptor.getValue();
        assertEquals(true, savedApartment.isDeleted());
        assertNull(savedApartment.getPersonalAccount());
    }
}