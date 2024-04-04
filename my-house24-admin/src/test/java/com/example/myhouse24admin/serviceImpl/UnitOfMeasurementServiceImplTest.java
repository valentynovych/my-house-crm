package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.UnitOfMeasurement;
import com.example.myhouse24admin.exception.ServiceAlreadyUsedException;
import com.example.myhouse24admin.mapper.UnitOfMeasurementMapper;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDto;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDtoListWrap;
import com.example.myhouse24admin.repository.InvoiceItemRepo;
import com.example.myhouse24admin.repository.UnitOfMeasurementRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitOfMeasurementServiceImplTest {

    @Mock
    private UnitOfMeasurementRepo unitOfMeasurementRepo;
    @Mock
    private InvoiceItemRepo invoiceItemRepo;
    @Mock
    private UnitOfMeasurementMapper mapper;
    @InjectMocks
    private UnitOfMeasurementServiceImpl unitOfMeasurementService;

    private List<UnitOfMeasurement> unitOfMeasurements;
    private List<UnitOfMeasurementDto> unitOfMeasurementDtos;

    @Captor
    private ArgumentCaptor<List<UnitOfMeasurement>> captor;

    @BeforeEach
    void setUp() {
        unitOfMeasurements = new ArrayList<>();
        unitOfMeasurementDtos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            UnitOfMeasurement unitOfMeasurement = new UnitOfMeasurement();
            unitOfMeasurement.setId((long) i);
            unitOfMeasurement.setName("name" + i);
            unitOfMeasurement.setDeleted(false);
            unitOfMeasurements.add(unitOfMeasurement);

            UnitOfMeasurementDto unitOfMeasurementDto = new UnitOfMeasurementDto();
            unitOfMeasurementDto.setId((long) i);
            unitOfMeasurementDto.setName("name" + i);
            unitOfMeasurementDtos.add(unitOfMeasurementDto);
        }
    }

    @Test
    void getAllMeasurementUnits() {
        // when
        when(unitOfMeasurementRepo.findAllByDeletedFalse())
                .thenReturn(unitOfMeasurements);
        when(mapper.unitOfMeasurementListToUnitOfMeasurementDtoList(unitOfMeasurements))
                .thenReturn(unitOfMeasurementDtos);
        List<UnitOfMeasurementDto> allMeasurementUnits = unitOfMeasurementService.getAllMeasurementUnits();

        // then
        assertFalse(allMeasurementUnits.isEmpty());
        assertEquals(5, allMeasurementUnits.size());
    }

    @Test
    void updateMeasurementUnist_WithoutUnitsToDelete() {
        // given
        UnitOfMeasurementDtoListWrap measurementListWrap = new UnitOfMeasurementDtoListWrap();
        measurementListWrap.setUnitOfMeasurements(unitOfMeasurementDtos);

        // when
        when(mapper.unitOfMeasurementListDtoToUnitOfMeasurementList(unitOfMeasurementDtos))
                .thenReturn(unitOfMeasurements);

        unitOfMeasurementService.updateMeasurementUnist(measurementListWrap);

        // then
        verify(unitOfMeasurementRepo).saveAll(unitOfMeasurements);
    }

    @Test
    void updateMeasurementUnist_WhenHaveUnitsToDelete_UnitsNotUsedInInvoices() {
        // given
        UnitOfMeasurementDtoListWrap measurementListWrap = new UnitOfMeasurementDtoListWrap();
        measurementListWrap.setUnitOfMeasurements(unitOfMeasurementDtos);
        measurementListWrap.setUnitsToDelete(List.of(0L, 1L, 2L, 3L, 4L));

        // when
        when(mapper.unitOfMeasurementListDtoToUnitOfMeasurementList(unitOfMeasurementDtos))
                .thenReturn(unitOfMeasurements);
        when(invoiceItemRepo.existsInvoiceItemByService_UnitOfMeasurement_Id(any(Long.class)))
                .thenReturn(false);
        when(unitOfMeasurementRepo.findAllById(eq(measurementListWrap.getUnitsToDelete())))
                .thenReturn(unitOfMeasurements);

        unitOfMeasurementService.updateMeasurementUnist(measurementListWrap);

        // then
        verify(unitOfMeasurementRepo).deleteAll(captor.capture());

        captor.getValue().forEach(unitOfMeasurement -> {
            assertTrue(unitOfMeasurement.isDeleted());
        });
    }

    @Test
    void updateMeasurementUnist_WhenHaveUnitsToDelete_UnitsIsUsedInInvoices() {
        // given
        UnitOfMeasurementDtoListWrap measurementListWrap = new UnitOfMeasurementDtoListWrap();
        measurementListWrap.setUnitOfMeasurements(unitOfMeasurementDtos);
        measurementListWrap.setUnitsToDelete(List.of(0L, 1L, 2L, 3L, 4L));

        // when
        when(mapper.unitOfMeasurementListDtoToUnitOfMeasurementList(unitOfMeasurementDtos))
                .thenReturn(unitOfMeasurements);
        when(invoiceItemRepo.existsInvoiceItemByService_UnitOfMeasurement_Id(any(Long.class)))
                .thenReturn(true);

        assertThrows(ServiceAlreadyUsedException.class,
                () -> unitOfMeasurementService.updateMeasurementUnist(measurementListWrap));
    }
}