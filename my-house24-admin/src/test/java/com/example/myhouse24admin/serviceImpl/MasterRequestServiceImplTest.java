package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.MasterRequest;
import com.example.myhouse24admin.entity.MasterRequestStatus;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.mapper.MasterRequestMapper;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import com.example.myhouse24admin.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestEditRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestResponse;
import com.example.myhouse24admin.model.masterRequest.MasterRequestTableResponse;
import com.example.myhouse24admin.model.staff.StaffShortResponse;
import com.example.myhouse24admin.repository.MasterRequestRepo;
import com.example.myhouse24admin.specification.MasterRequestSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MasterRequestServiceImplTest {

    @Mock
    private MasterRequestRepo masterRequestRepo;
    @Mock
    private MasterRequestMapper masterRequestMapper;
    @InjectMocks
    private MasterRequestServiceImpl masterRequestService;
    private static MasterRequest masterRequest;

    @BeforeEach
    void setUp() {
        masterRequest = new MasterRequest();
        masterRequest.setId(1L);
        masterRequest.setStatus(MasterRequestStatus.NEW);
        masterRequest.setDeleted(false);
        masterRequest.setApartment(new Apartment());
        masterRequest.setApartmentOwnerPhone("Test phone");
        masterRequest.setMasterType("PLUMBER");
        masterRequest.setComment("Test comment");
        masterRequest.setDescription("Test description");
        masterRequest.setStaff(new Staff());
        masterRequest.setCreationDate(Instant.now());
        masterRequest.setVisitDate(Instant.now().plus(1, ChronoUnit.DAYS));
    }

    @Test
    void addNewMasterRequest() {
        // given
        MasterRequestAddRequest request = new MasterRequestAddRequest();
        request.setMasterType(masterRequest.getMasterType());
        request.setComment(masterRequest.getComment());
        request.setDescription(masterRequest.getDescription());
        request.setApartmentOwnerPhone(masterRequest.getApartmentOwnerPhone());
        request.setApartmentId(1L);
        request.setApartmentOwnerId(1L);
        request.setStatus(MasterRequestStatus.NEW);
        request.setVisitDate(masterRequest.getVisitDate());

        // when
        when(masterRequestMapper.masterRequestAddRequestToMasterRequest(request))
                .thenReturn(masterRequest);
        when(masterRequestRepo.save(any(MasterRequest.class)))
                .thenReturn(masterRequest);

        // call the method
        masterRequestService.addNewMasterRequest(request);

        // then
        verify(masterRequestMapper, times(1)).masterRequestAddRequestToMasterRequest(request);
        verify(masterRequestRepo, times(1)).save(any(MasterRequest.class));
    }

    @Test
    void getMasterRequests() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("status", MasterRequestStatus.NEW.name());
        List<MasterRequest> masterRequestList = List.of(masterRequest, masterRequest);
        Page<MasterRequest> masterRequestPage = new PageImpl<>(masterRequestList, pageable, 2L);

        MasterRequestTableResponse tableResponse = new MasterRequestTableResponse(
                masterRequest.getId(),
                masterRequest.getVisitDate(),
                masterRequest.getDescription(),
                new ApartmentResponse(),
                masterRequest.getApartmentOwnerPhone(),
                new StaffShortResponse(),
                masterRequest.getStatus(),
                masterRequest.getMasterType()
        );

        // when
        when(masterRequestRepo.findAll(any(MasterRequestSpecification.class), any(Pageable.class)))
                .thenReturn(masterRequestPage);
        when(masterRequestMapper.masterRequestListToMasterRequestTableResponseList(eq(masterRequestList)))
                .thenReturn(List.of(tableResponse, tableResponse));

        // call the method
        Page<MasterRequestTableResponse> masterRequests =
                masterRequestService.getMasterRequests(pageable.getPageNumber(), pageable.getPageSize(), searchParams);

        // then
        assertFalse(masterRequests.isEmpty());
        assertEquals(2, masterRequests.getTotalElements());
        assertEquals(tableResponse, masterRequests.getContent().get(0));

        verify(masterRequestRepo, times(1)).findAll(any(MasterRequestSpecification.class), any(Pageable.class));
        verify(masterRequestMapper, times(1)).masterRequestListToMasterRequestTableResponseList(eq(masterRequestList));
    }

    @Test
    void deleteMasterRequestById() {
        // given
        Long masterRequestId = 1L;

        // when
        when(masterRequestRepo.findByIdAndDeletedIsFalse(eq(masterRequestId)))
                .thenReturn(Optional.of(masterRequest));

        // call the method
        boolean deleted = masterRequestService.deleteMasterRequestById(masterRequestId);

        // then
        assertTrue(deleted);

        verify(masterRequestRepo, times(1)).findByIdAndDeletedIsFalse(eq(masterRequestId));
        verify(masterRequestRepo, times(1)).delete(any(MasterRequest.class));
    }

    @Test
    void deleteMasterRequestById_WhenMasterRequestNotFound() {
        // given
        Long masterRequestId = 1L;

        // when
        when(masterRequestRepo.findByIdAndDeletedIsFalse(eq(masterRequestId)))
                .thenReturn(Optional.empty());

        // call the method
        assertThrows(EntityNotFoundException.class, () -> masterRequestService.deleteMasterRequestById(masterRequestId));

        verify(masterRequestRepo, times(1)).findByIdAndDeletedIsFalse(eq(masterRequestId));
    }

    @Test
    void deleteMasterRequestById_WhenMasterRequestHaveStatusInProgress() {
        // given
        Long masterRequestId = 1L;
        masterRequest.setStatus(MasterRequestStatus.IN_PROGRESS);

        // when
        when(masterRequestRepo.findByIdAndDeletedIsFalse(eq(masterRequestId)))
                .thenReturn(Optional.of(masterRequest));

        // call the method
        boolean deleted = masterRequestService.deleteMasterRequestById(masterRequestId);

        // then
        assertFalse(deleted);

        verify(masterRequestRepo, times(1)).findByIdAndDeletedIsFalse(eq(masterRequestId));
        verify(masterRequestRepo, never()).delete(any(MasterRequest.class));
    }

    @Test
    void getMasterRequestById() {
        // given
        Long masterRequestId = 1L;
        MasterRequestResponse masterRequestResponse = new MasterRequestResponse(
                masterRequest.getId(),
                masterRequest.getVisitDate(),
                masterRequest.getDescription(),
                new ApartmentResponse(),
                masterRequest.getApartmentOwnerPhone(),
                new StaffShortResponse(),
                masterRequest.getStatus(),
                masterRequest.getCreationDate(),
                masterRequest.getMasterType()
        );

        // when
        when(masterRequestRepo.findByIdAndDeletedIsFalse(eq(masterRequestId)))
                .thenReturn(Optional.of(masterRequest));
        when(masterRequestMapper.masterRequestToMasterRequestResponse(eq(masterRequest)))
                .thenReturn(masterRequestResponse);

        // call the method
        MasterRequestResponse masterRequestById = masterRequestService.getMasterRequestById(masterRequestId);

        // then
        assertEquals(masterRequestResponse, masterRequestById);

        verify(masterRequestRepo, times(1)).findByIdAndDeletedIsFalse(eq(masterRequestId));
        verify(masterRequestMapper, times(1)).masterRequestToMasterRequestResponse(eq(masterRequest));
    }

    @Test
    void updateMasterRequest() {
        // given
        Long masterRequestId = 1L;
        MasterRequestEditRequest request = new MasterRequestEditRequest();
        request.setId(1L);
        request.setApartmentId(1L);
        request.setVisitDate(Instant.now());
        request.setMasterType("PLUMBER");
        request.setDescription("testDescription");
        request.setComment("testComment");
        request.setApartmentOwnerId(1L);
        request.setStatus(MasterRequestStatus.NEW);

        // when
        when(masterRequestRepo.findByIdAndDeletedIsFalse(eq(masterRequestId)))
                .thenReturn(Optional.of(masterRequest));
        doNothing().when(masterRequestMapper)
                .updateMasterRequestFromMasterRequestEditRequest(any(MasterRequest.class), any(MasterRequestEditRequest.class));

        // call the method
        masterRequestService.updateMasterRequest(masterRequestId, request);

        // then
        verify(masterRequestRepo, times(1)).findByIdAndDeletedIsFalse(eq(masterRequestId));
        verify(masterRequestMapper, times(1)).updateMasterRequestFromMasterRequestEditRequest(any(MasterRequest.class), any(MasterRequestEditRequest.class));
        verify(masterRequestRepo, times(1)).save(any(MasterRequest.class));
    }
}