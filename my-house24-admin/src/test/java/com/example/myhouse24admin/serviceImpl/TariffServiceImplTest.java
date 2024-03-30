package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.entity.Tariff;
import com.example.myhouse24admin.entity.TariffItem;
import com.example.myhouse24admin.entity.UnitOfMeasurement;
import com.example.myhouse24admin.exception.TariffAlreadyUsedException;
import com.example.myhouse24admin.mapper.TariffMapper;
import com.example.myhouse24admin.model.invoices.TariffNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.model.services.ServiceResponse;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDto;
import com.example.myhouse24admin.model.tariffs.*;
import com.example.myhouse24admin.repository.InvoiceRepo;
import com.example.myhouse24admin.repository.TariffItemRepo;
import com.example.myhouse24admin.repository.TariffRepo;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TariffServiceImplTest {

    @Mock
    private TariffRepo tariffRepo;
    @Mock
    private TariffItemRepo tariffItemRepo;
    @Mock
    private TariffMapper mapper;
    @Mock
    private InvoiceRepo invoiceRepo;
    @InjectMocks
    private TariffServiceImpl tariffService;
    private TariffRequest tariffRequest;
    private Tariff tariff;
    private TariffResponse tariffResponse;

    @BeforeEach
    void setUp() {
        tariffRequest = new TariffRequest();
        tariff = new Tariff();
        tariff.setId(1L);
        tariff.setName("test tariff");
        tariff.setDescription("test description");
        tariff.setLastModify(Instant.now());


        tariffRequest.setId(tariff.getId());
        tariffRequest.setName(tariff.getName());
        tariffRequest.setDescription(tariff.getDescription());

        List<TariffItem> tariffItems = new ArrayList<>();
        List<TariffItemRequest> tariffItemRequests = new ArrayList<>();
        List<TariffItemResponse> tariffItemResponses = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            TariffItem tariffItem = new TariffItem();
            tariffItem.setId((long) i);
            Service service = new Service();
            service.setId((long) i);
            service.setName("test service" + i);
            UnitOfMeasurement unitOfMeasurement = new UnitOfMeasurement();
            unitOfMeasurement.setId(1L);
            unitOfMeasurement.setName("m3");
            service.setUnitOfMeasurement(unitOfMeasurement);

            tariffItem.setService(service);
            tariffItem.setCurrency("UAH");
            tariffItem.setTariff(tariff);
            tariffItems.add(tariffItem);

            TariffItemRequest tariffItemRequest = new TariffItemRequest();
            tariffItemRequest.setId(1L);
            tariffItemRequest.setServiceId((long) i);
            tariffItemRequest.setServicePrice(BigDecimal.valueOf((i + 1) * 2.12));
            tariffItemRequests.add(tariffItemRequest);

            ServiceResponse serviceResponse = new ServiceResponse(
                    service.getId(),
                    service.getName(),
                    service.isShowInMeter(),
                    new UnitOfMeasurementDto(
                            service.getUnitOfMeasurement().getId(),
                            service.getUnitOfMeasurement().getName())
            );

            TariffItemResponse tariffItemResponse = new TariffItemResponse(
                    tariffItem.getId(),
                    tariffItem.getServicePrice(),
                    tariffItem.getCurrency(),
                    serviceResponse
            );
            tariffItemResponses.add(tariffItemResponse);
        }
        tariff.setTariffItems(tariffItems);
        tariffRequest.setTariffItems(tariffItemRequests);

        tariffResponse = new TariffResponse(
                tariff.getId(),
                tariff.getName(),
                tariff.getDescription(),
                tariff.getLastModify(),
                tariffItemResponses);
    }

    @Test
    void addNewTariff() {
        // given
        TariffRequestWrap tariffRequestWrap = new TariffRequestWrap();
        tariffRequestWrap.setTariffRequest(tariffRequest);
        tariff.getTariffItems().forEach(tariffItem -> tariffItem.setTariff(null));
        ArgumentCaptor<Tariff> tariffCaptor = ArgumentCaptor.forClass(Tariff.class);

        // when
        when(mapper.tariffRequestToTariff(tariffRequest))
                .thenReturn(tariff);
        when(tariffRepo.save(tariffCaptor.capture()))
                .thenReturn(tariff);

        tariffService.addNewTariff(tariffRequestWrap);

        // then
        verify(tariffRepo).save(tariffCaptor.capture());
        tariffCaptor.getValue().getTariffItems().forEach(tariffItem -> {
            assertNotNull(tariffItem.getTariff());
        });
    }

    @Test
    void getAllTariffs() {
        // given

        // when
        when(tariffRepo.findAllByDeletedIsFalse(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(tariff)));
        when(mapper.tariffListToTariffResponseList(anyList()))
                .thenReturn(List.of(tariffResponse));

        Page<TariffResponse> allTariffs = tariffService.getAllTariffs(0, 10);
        assertFalse(allTariffs.isEmpty());
        assertEquals(1, allTariffs.getContent().size());

        // then
        verify(tariffRepo).findAllByDeletedIsFalse(any(Pageable.class));
        verify(mapper).tariffListToTariffResponseList(anyList());

    }

    @Test
    void getTariffById_WhenTariffExists() {
        // when
        when(tariffRepo.findById(tariff.getId()))
                .thenReturn(Optional.of(tariff));
        when(mapper.tariffToTariffResponse(tariff))
                .thenReturn(tariffResponse);

        TariffResponse tariffById = tariffService.getTariffById(tariff.getId());

        // then
        verify(tariffRepo).findById(tariff.getId());
        verify(mapper).tariffToTariffResponse(tariff);
        assertEquals(tariffResponse, tariffById);
    }

    @Test
    void getTariffById_WhenTariffNotExists() {
        // when
        when(tariffRepo.findById(tariff.getId()))
                .thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> tariffService.getTariffById(tariff.getId()));
        verify(tariffRepo).findById(tariff.getId());
    }

    @Test
    void editTariff_WhenTariffNotHaveTariffItemsToDelete() {
        // given
        tariff.getTariffItems().forEach(tariffItem -> tariffItem.setTariff(null));
        ArgumentCaptor<Tariff> tariffCaptor = ArgumentCaptor.forClass(Tariff.class);
        TariffRequestWrap tariffRequestWrap = new TariffRequestWrap();
        tariffRequestWrap.setTariffRequest(tariffRequest);

        // when
        when(tariffRepo.findById(tariff.getId()))
                .thenReturn(Optional.of(tariff));

        tariffService.editTariff(tariff.getId(), tariffRequestWrap);

        // then
        verify(tariffRepo).findById(tariff.getId());
        verify(mapper).updateTariffFromTariffRequest(tariff, tariffRequest);
        verify(tariffRepo).save(tariffCaptor.capture());

        tariffCaptor.getValue().getTariffItems().forEach(tariffItem -> {
            assertNotNull(tariffItem.getTariff());
        });
    }

    @Test
    void editTariff_WhenTariffHaveTariffItemsToDelete() {
        // given
        tariff.getTariffItems().forEach(tariffItem -> tariffItem.setTariff(null));
        ArgumentCaptor<Tariff> tariffCaptor = ArgumentCaptor.forClass(Tariff.class);
        TariffRequestWrap tariffRequestWrap = new TariffRequestWrap();
        tariffRequestWrap.setTariffRequest(tariffRequest);
        tariffRequestWrap.setTariffItemToDelete(List.of(1L, 2L, 3L));

        // when
        when(tariffRepo.findById(tariff.getId()))
                .thenReturn(Optional.of(tariff));

        tariffService.editTariff(tariff.getId(), tariffRequestWrap);

        // then
        verify(tariffItemRepo).deleteAllById(eq(List.of(1L, 2L, 3L)));
        verify(tariffRepo).findById(tariff.getId());
        verify(mapper).updateTariffFromTariffRequest(tariff, tariffRequest);
        verify(tariffRepo).save(tariffCaptor.capture());

        tariffCaptor.getValue().getTariffItems().forEach(tariffItem -> {
            assertNotNull(tariffItem.getTariff());
        });
    }

    @Test
    void deleteTariffById_WhenTariffNotUsedInInvoice() {
        // given
        ArgumentCaptor<Tariff> tariffCaptor = ArgumentCaptor.forClass(Tariff.class);

        // when
        when(tariffRepo.findById(tariff.getId()))
                .thenReturn(Optional.of(tariff));
        when(invoiceRepo.existsInvoiceByApartment_Tariff_Id(eq(tariff.getId())))
                .thenReturn(false);

        boolean deleted = tariffService.deleteTariffById(tariff.getId());

        // then
        verify(tariffRepo).findById(tariff.getId());
        verify(invoiceRepo).existsInvoiceByApartment_Tariff_Id(eq(tariff.getId()));
        verify(tariffRepo).save(tariffCaptor.capture());

        assertTrue(deleted);
        assertTrue(tariffCaptor.getValue().isDeleted());
    }

    @Test
    void deleteTariffById_WhenTariffIsUsedInInvoice() {
        // when
        when(tariffRepo.findById(tariff.getId()))
                .thenReturn(Optional.of(tariff));
        when(invoiceRepo.existsInvoiceByApartment_Tariff_Id(eq(tariff.getId())))
                .thenReturn(true);

        assertThrows(TariffAlreadyUsedException.class, () -> tariffService.deleteTariffById(tariff.getId()));

        // then
        verify(tariffRepo).findById(tariff.getId());
        verify(invoiceRepo).existsInvoiceByApartment_Tariff_Id(eq(tariff.getId()));
    }

    @Test
    void getTariffsForSelect() {
        // given
        SelectSearchRequest selectSearchRequest = new SelectSearchRequest("search", 1);
        TariffNameResponse tariffNameResponse = new TariffNameResponse(tariff.getId(), tariff.getName());

        // when
        when(tariffRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(tariff), PageRequest.of(0, 10), 1));
        when(mapper.tariffListToTariffNameResponseList(anyList()))
                .thenReturn(List.of(tariffNameResponse));

        Page<TariffNameResponse> tariffsForSelect = tariffService.getTariffsForSelect(selectSearchRequest);

        // then
        verify(tariffRepo).findAll(any(Specification.class), any(Pageable.class));
        verify(mapper).tariffListToTariffNameResponseList(anyList());

        assertFalse(tariffsForSelect.isEmpty());
        assertEquals(1, tariffsForSelect.getTotalElements());
    }

    @Test
    void getTariffItems() {
        // given
        List<com.example.myhouse24admin.model.invoices.TariffItemResponse> tariffItemResponses = new ArrayList<>();
        for (TariffItem tariffItem : tariff.getTariffItems())
            tariffItemResponses.add(new com.example.myhouse24admin.model.invoices.TariffItemResponse(
                    tariffItem.getService().getId(),
                    tariffItem.getService().getName(),
                    tariffItem.getService().getUnitOfMeasurement().getName(),
                    tariffItem.getServicePrice()));

        // when
        when(tariffItemRepo.findAll(any(Specification.class)))
                .thenReturn(tariff.getTariffItems());
        when(mapper.tariffItemListToTariffItemResponse(anyList()))
                .thenReturn(tariffItemResponses);

        List<com.example.myhouse24admin.model.invoices.TariffItemResponse> tariffItems = tariffService.getTariffItems(tariff.getId());

        // then
        verify(tariffItemRepo).findAll(any(Specification.class));
        verify(mapper).tariffItemListToTariffItemResponse(anyList());

        assertFalse(tariffItems.isEmpty());
        assertEquals(3, tariffItems.size());
    }
}