package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.MeterReading;
import com.example.myhouse24admin.entity.MeterReadingStatus;
import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.mapper.MeterReadingMapper;
import com.example.myhouse24admin.model.meterReadings.*;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.MeterReadingRepo;
import com.example.myhouse24admin.repository.ServicesRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeterReadingServiceImplTest {
    @Mock
    private MeterReadingRepo meterReadingRepo;
    @Mock
    private ApartmentRepo apartmentRepo;
    @Mock
    private ServicesRepo servicesRepo;
    @Mock
    private MeterReadingMapper meterReadingMapper;
    @InjectMocks
    private MeterReadingServiceImpl meterReadingService;
    private static MeterReadingRequest meterReadingRequest;
    private static ApartmentMeterReadingResponse apartmentMeterReadingResponse;
    private static MeterReading meterReading;
    @BeforeAll
    public static void setUp(){
        meterReadingRequest = new MeterReadingRequest("creation",
                MeterReadingStatus.NEW, BigDecimal.valueOf(23), 1L,
                1L);
        apartmentMeterReadingResponse = new ApartmentMeterReadingResponse(1L,
                "number", MeterReadingStatus.NEW, "12.03.1990",
                "house", "section", "apartment",
                "service", BigDecimal.valueOf(23), "measurement");

        meterReading = new MeterReading();
        meterReading.setReadings(BigDecimal.valueOf(44));
    }

    @Test
    void createMeterReading_Should_Create_Reading() {
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.of(new Apartment()));
        when(servicesRepo.findById(anyLong())).thenReturn(Optional.of(new Service()));
        when(meterReadingRepo.findLast()).thenReturn(Optional.empty());
        when(meterReadingMapper
                .meterReadingRequestToMeterReading(any(MeterReadingRequest.class),
                        any(Apartment.class), any(Service.class), anyString()))
                .thenReturn(new MeterReading());
        when(meterReadingRepo.save(any(MeterReading.class)))
                .thenReturn(new MeterReading());

        meterReadingService.createMeterReading(meterReadingRequest);

        verify(apartmentRepo, times(1)).findById(anyLong());
        verify(servicesRepo, times(1)).findById(anyLong());
        verify(meterReadingRepo, times(1)).findLast();
        verify(meterReadingMapper, times(1))
                .meterReadingRequestToMeterReading(any(MeterReadingRequest.class),
                any(Apartment.class), any(Service.class), anyString());
        verify(meterReadingRepo, times(1)).save(any(MeterReading.class));

        verifyNoMoreInteractions(apartmentRepo);
        verifyNoMoreInteractions(servicesRepo);
        verifyNoMoreInteractions(meterReadingRepo);
        verifyNoMoreInteractions(meterReadingMapper);
    }
    @Test
    void createMeterReading_Apartment_FindById_Should_Throw_EntityNotFoundException() {
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meterReadingService
                .createMeterReading(meterReadingRequest));

        verify(apartmentRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(apartmentRepo);
    }
    @Test
    void createMeterReading_Service_FindById_Should_Throw_EntityNotFoundException() {
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.of(new Apartment()));
        when(servicesRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meterReadingService
                .createMeterReading(meterReadingRequest));

        verify(apartmentRepo, times(1)).findById(anyLong());
        verify(servicesRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(apartmentRepo);
        verifyNoMoreInteractions(servicesRepo);
    }

    @Test
    void createNumber_Should_Create_First_Number() {
        when(meterReadingRepo.findLast()).thenReturn(Optional.empty());

        String number = meterReadingService.createNumber();
        assertThat(number).isEqualTo("0000000001");

        verify(meterReadingRepo, times(1)).findLast();
        verifyNoMoreInteractions(meterReadingRepo);
    }
    @Test
    void createNumber_Should_Create_Second_Number() {
        MeterReading meterReading = new MeterReading();
        meterReading.setNumber("0000000001");
        when(meterReadingRepo.findLast()).thenReturn(Optional.of(meterReading));

        String number = meterReadingService.createNumber();
        assertThat(number).isEqualTo("0000000002");

        verify(meterReadingRepo, times(1)).findLast();
        verifyNoMoreInteractions(meterReadingRepo);
    }
    @Test
    void getMeterReadingResponsesForTable() {
        Pageable pageable = PageRequest.of(0,1);
        TableMeterReadingResponse tableMeterReadingResponse = new TableMeterReadingResponse(1L,
                1L, "house", "section",
                "apartment", "service", BigDecimal.valueOf(23),
                "measurement");
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("page", "0");
        requestMap.put("pageSize", "1");
        requestMap.put("sectionId", "1");
        requestMap.put("houseId", "1");
        requestMap.put("serviceId", "1");
        requestMap.put("apartment", "1");


        when(meterReadingRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new MeterReading(), pageable, 5)));
        when(meterReadingMapper.meterReadingListToTableMeterReadingResponseList(anyList()))
                .thenReturn(List.of(tableMeterReadingResponse));

        Page<TableMeterReadingResponse> tableMeterReadingResponsePage = meterReadingService
                .getMeterReadingResponsesForTable(requestMap);

        assertThat(tableMeterReadingResponsePage.getContent()).hasSize(1);
        assertThat(tableMeterReadingResponsePage.getContent().get(0)).usingRecursiveComparison()
                .isEqualTo(tableMeterReadingResponse);

        verify(meterReadingRepo, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
        verify(meterReadingMapper, times(1))
                .meterReadingListToTableMeterReadingResponseList(anyList());

        verifyNoMoreInteractions(meterReadingRepo);
        verifyNoMoreInteractions(meterReadingMapper);
    }

    @Test
    void getMeterReadingResponse_Should_Return_MeterReadingResponse() {
        MeterReadingResponse expectedMeterReadingResponse = new MeterReadingResponse("number",
                "date", BigDecimal.valueOf(23),
                MeterReadingStatus.NEW, new HouseNameResponse(1L, "name"),
                new SectionNameResponse(1L, "name"),
                new ApartmentNumberResponse(1L, "name"),
                new ServiceNameResponse(1L, "name"));
        when(meterReadingRepo.findById(anyLong())).thenReturn(Optional.of(new MeterReading()));
        when(meterReadingMapper.meterReadingToMeterReadingResponse(any(MeterReading.class)))
                .thenReturn(expectedMeterReadingResponse);

        MeterReadingResponse meterReadingResponse = meterReadingService.getMeterReadingResponse(1L);
        assertThat(meterReadingResponse).usingRecursiveComparison()
                .isEqualTo(expectedMeterReadingResponse);

        verify(meterReadingRepo, times(1)).findById(anyLong());
        verify(meterReadingMapper, times(1))
                .meterReadingToMeterReadingResponse(any(MeterReading.class));

        verifyNoMoreInteractions(meterReadingRepo);
        verifyNoMoreInteractions(meterReadingMapper);
    }

    @Test
    void getMeterReadingResponse_Should_Throw_EntityNotFoundException() {
        when(meterReadingRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meterReadingService
                .getMeterReadingResponse(2L));

        verify(meterReadingRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(meterReadingRepo);
    }
    @Test
    void updateMeterReading_Should_Update_MeterReading() {
        when(meterReadingRepo.findById(anyLong())).thenReturn(Optional.of(new MeterReading()));
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.of(new Apartment()));
        when(servicesRepo.findById(anyLong())).thenReturn(Optional.of(new Service()));
        doNothing().when(meterReadingMapper).updateMeterReading(any(MeterReading.class),
                any(MeterReadingRequest.class), any(Apartment.class), any(Service.class));
        when(meterReadingRepo.save(any(MeterReading.class))).thenReturn(new MeterReading());

        meterReadingService.updateMeterReading(1L, meterReadingRequest);

        verify(meterReadingRepo, times(1)).findById(anyLong());
        verify(apartmentRepo, times(1)).findById(anyLong());
        verify(servicesRepo, times(1)).findById(anyLong());
        verify(meterReadingRepo, times(1))
                .save(any(MeterReading.class));
        verify(meterReadingMapper, times(1))
                .updateMeterReading(any(MeterReading.class), any(MeterReadingRequest.class),
                        any(Apartment.class), any(Service.class));

        verifyNoMoreInteractions(meterReadingRepo);
        verifyNoMoreInteractions(apartmentRepo);
        verifyNoMoreInteractions(servicesRepo);
        verifyNoMoreInteractions(meterReadingMapper);
    }
    @Test
    void updateMeterReading_Reading_FindById_Should_Throw_EntityNotFoundException() {
        when(meterReadingRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meterReadingService
                .updateMeterReading(2L, meterReadingRequest));

        verify(meterReadingRepo, times(1)).findById(anyLong());
        verifyNoMoreInteractions(meterReadingRepo);

    }
    @Test
    void updateMeterReading_Apartment_FindById_Should_Throw_EntityNotFoundException() {
        when(meterReadingRepo.findById(anyLong())).thenReturn(Optional.of(new MeterReading()));
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meterReadingService
                .updateMeterReading(1L, meterReadingRequest));

        verify(apartmentRepo, times(1)).findById(anyLong());
        verify(meterReadingRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(meterReadingRepo);
        verifyNoMoreInteractions(apartmentRepo);
    }
    @Test
    void updateMeterReading_Service_FindById_Should_Throw_EntityNotFoundException() {
        when(meterReadingRepo.findById(anyLong())).thenReturn(Optional.of(new MeterReading()));
        when(apartmentRepo.findById(anyLong())).thenReturn(Optional.of(new Apartment()));
        when(servicesRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meterReadingService
                .updateMeterReading(1L, meterReadingRequest));

        verify(apartmentRepo, times(1)).findById(anyLong());
        verify(servicesRepo, times(1)).findById(anyLong());
        verify(meterReadingRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(meterReadingRepo);
        verifyNoMoreInteractions(apartmentRepo);
        verifyNoMoreInteractions(servicesRepo);
    }
    @Test
    void getApartmentMeterReadingResponses() {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("page", "0");
        requestMap.put("pageSize", "1");
        requestMap.put("sectionId", "1");
        requestMap.put("houseId", "1");
        requestMap.put("serviceId", "1");
        requestMap.put("apartment", "1");
        requestMap.put("number","22");
        requestMap.put("status","NEW");
        requestMap.put("creationDate","12.03.1990");

        mockForApartmentMeterReadingResponse();

        Page<ApartmentMeterReadingResponse> apartmentMeterReadingResponses = meterReadingService
                .getApartmentMeterReadingResponses(1L,requestMap);

        assertThat(apartmentMeterReadingResponses.getContent()).hasSize(1);
        assertThat(apartmentMeterReadingResponses.getContent().get(0)).usingRecursiveComparison()
                .isEqualTo(apartmentMeterReadingResponse);

        verifyForApartmentMeterReadingResponse();
    }

    @Test
    void deleteMeterReading() {
        when(meterReadingRepo.findById(anyLong())).thenReturn(Optional.of(new MeterReading()));
        when(meterReadingRepo.save(any(MeterReading.class))).thenReturn(new MeterReading());

        meterReadingService.deleteMeterReading(1L);

        verify(meterReadingRepo, times(1)).findById(anyLong());
        verify(meterReadingRepo, times(1)).save(any(MeterReading.class));

        verifyNoMoreInteractions(meterReadingRepo);
    }

    @Test
    void getMeterReadingResponsesForTableInInvoice() {

        mockForApartmentMeterReadingResponse();

        Page<ApartmentMeterReadingResponse> apartmentMeterReadingResponses = meterReadingService
                .getMeterReadingResponsesForTableInInvoice(0,1, 1L);

        assertThat(apartmentMeterReadingResponses.getContent()).hasSize(1);
        assertThat(apartmentMeterReadingResponses.getContent().get(0)).usingRecursiveComparison()
                .isEqualTo(apartmentMeterReadingResponse);

        verifyForApartmentMeterReadingResponse();

    }

    private void mockForApartmentMeterReadingResponse(){
        Pageable pageable = PageRequest.of(0,1);
        when(meterReadingRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new MeterReading(), pageable, 5)));
        when(meterReadingMapper.meterReadingListToApartmentMeterReadingResponseList(anyList()))
                .thenReturn(List.of(apartmentMeterReadingResponse));
    }

    private void verifyForApartmentMeterReadingResponse(){
        verify(meterReadingRepo, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
        verify(meterReadingMapper, times(1))
                .meterReadingListToApartmentMeterReadingResponseList(anyList());

        verifyNoMoreInteractions(meterReadingRepo);
        verifyNoMoreInteractions(meterReadingMapper);
    }

    @Test
    void getAmountOfConsumptions_Should_Find_Two_Readings() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "creationDate"));
        MeterReading meterReading1 = new MeterReading();
        meterReading1.setReadings(BigDecimal.valueOf(20));

        when(meterReadingRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(meterReading, meterReading1), pageable, 5));

        List<BigDecimal> amounts = meterReadingService.getAmountOfConsumptions(new Long[]{1L}, 2L);

        assertThat(amounts).hasSize(1);
        assertThat(amounts.get(0)).isEqualTo(BigDecimal.valueOf(24));

        verify(meterReadingRepo, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(meterReadingRepo);
    }

    @Test
    void getAmountOfConsumptions_Should_Find_One_Reading() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "creationDate"));

        when(meterReadingRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(meterReading), pageable, 5));

        List<BigDecimal> amounts = meterReadingService.getAmountOfConsumptions(new Long[]{1L}, 2L);

        assertThat(amounts).hasSize(1);
        assertThat(amounts.get(0)).isEqualTo(BigDecimal.valueOf(44));

        verify(meterReadingRepo, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(meterReadingRepo);
    }
    @Test
    void getAmountOfConsumptions_Should_Not_Find_Readings() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "creationDate"));

        when(meterReadingRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 5));

        List<BigDecimal> amounts = meterReadingService.getAmountOfConsumptions(new Long[]{1L}, 2L);

        assertThat(amounts).hasSize(1);
        assertThat(amounts.get(0)).isEqualTo(BigDecimal.valueOf(0));

        verify(meterReadingRepo, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(meterReadingRepo);
    }
}