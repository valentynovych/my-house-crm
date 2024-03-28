package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.MasterRequest;
import com.example.myhouse24user.entity.MasterRequestStatus;
import com.example.myhouse24user.mapper.MasterRequestMapper;
import com.example.myhouse24user.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24user.model.masterRequest.MasterRequestTableResponse;
import com.example.myhouse24user.repository.MasterRequestRepo;
import com.example.myhouse24user.service.ApartmentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.myhouse24user.config.TestConfig.USER_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MasterRequestServiceImplTest {

    @Mock
    private ApartmentService apartmentService;
    @Mock
    private MasterRequestRepo masterRequestRepo;
    @Mock
    private MasterRequestMapper masterRequestMapper;
    @InjectMocks
    private MasterRequestServiceImpl masterRequestService;
    private Apartment apartment;
    private ArrayList<MasterRequest> masterRequests;
    private List<MasterRequestTableResponse> masterRequestTableResponses;

    @BeforeEach
    void setUp() {
        this.masterRequests = new ArrayList<>();
        this.masterRequestTableResponses = new ArrayList<>();
        this.apartment = new Apartment();
        apartment.setId(1L);
        apartment.setApartmentNumber("00001");

        for (int i = 0; i < 5; i++) {
            var masterRequest = new MasterRequest();
            masterRequest.setId((long) i);
            masterRequest.setStatus(MasterRequestStatus.NEW);
            masterRequest.setVisitDate(Instant.now().plus(i, ChronoUnit.DAYS));
            masterRequest.setApartment(apartment);
            masterRequest.setCreationDate(Instant.now());
            masterRequest.setMasterType("PLUMBER");
            masterRequest.setDescription("description" + i);
            masterRequests.add(masterRequest);

            masterRequestTableResponses.add(new MasterRequestTableResponse(
                    (long) i,
                    masterRequest.getVisitDate(),
                    masterRequest.getStatus(),
                    masterRequest.getDescription(),
                    masterRequest.getMasterType()));
        }
    }

    @Test
    void getMasterRequests() {
        // when
        when(masterRequestRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(masterRequests, PageRequest.of(0, 10), masterRequests.size()));
        when(masterRequestMapper.masterRequestListToMasterRequestTableResponseList(masterRequests))
                .thenReturn(masterRequestTableResponses);

        Page<MasterRequestTableResponse> requestServiceMasterRequests =
                masterRequestService.getMasterRequests(USER_EMAIL, 0, 10);
        // then
        List<MasterRequestTableResponse> content = requestServiceMasterRequests.getContent();

        assertFalse(content.isEmpty());
        assertEquals(5, requestServiceMasterRequests.getNumberOfElements());

        MasterRequestTableResponse masterRequestTableResponse = content.get(0);
        assertEquals(masterRequestTableResponse.masterType(), "PLUMBER");
        assertEquals(masterRequestTableResponse.description(), "description0");
    }

    @Test
    void addMasterRequest() {
        // given
        Instant now = Instant.now();
        var masterRequestAddRequest = new MasterRequestAddRequest();
        masterRequestAddRequest.setMasterType("PLUMBER");
        masterRequestAddRequest.setDescription("description");
        masterRequestAddRequest.setApartmentId(1L);
        masterRequestAddRequest.setVisitDate(now);

        // when
        when(apartmentService.findApartmentByIdAndOwner(anyLong(), eq(USER_EMAIL)))
                .thenReturn(apartment);
        when(masterRequestMapper.masterRequestAddRequestToMasterRequest(masterRequestAddRequest, apartment))
                .thenReturn(masterRequests.get(0));

        masterRequestService.addMasterRequest(masterRequestAddRequest, USER_EMAIL);

        // then
        verify(masterRequestRepo).save(masterRequests.get(0));
    }

    @Test
    void deleteMasterRequest_WhenFound() {
        // when
        when(masterRequestRepo.findById(1L)).thenReturn(Optional.of(masterRequests.get(0)))
                .thenReturn(Optional.of(masterRequests.get(1)));
        masterRequestService.deleteMasterRequest(1L);
        ArgumentCaptor<MasterRequest> argumentCaptor = ArgumentCaptor.forClass(MasterRequest.class);

        // then
        verify(masterRequestRepo, times(1)).save(argumentCaptor.capture());
        assertEquals(MasterRequestStatus.CANCELED, argumentCaptor.getValue().getStatus());
        clearInvocations(masterRequestRepo);
    }

    @Test
    void deleteMasterRequest_WhenNotFound() {
        // when
        when(masterRequestRepo.findById(1L)).thenReturn(Optional.empty())
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> masterRequestService.deleteMasterRequest(1L));
    }
}