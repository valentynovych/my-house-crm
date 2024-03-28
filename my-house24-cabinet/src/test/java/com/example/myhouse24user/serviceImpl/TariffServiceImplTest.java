package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.*;
import com.example.myhouse24user.mapper.TariffMapper;
import com.example.myhouse24user.model.tariff.TariffItemResponse;
import com.example.myhouse24user.model.tariff.TariffResponse;
import com.example.myhouse24user.repository.ApartmentRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class TariffServiceImplTest {

    @Mock
    private ApartmentRepo apartmentRepo;
    @Mock
    private TariffMapper tariffMapper;
    @InjectMocks
    private TariffServiceImpl tariffServiceImpl;

    private Tariff tariff;
    private TariffResponse tariffResponse;
    private Apartment apartment;

    @BeforeEach
    void setUp() {

        tariff = new Tariff();
        tariff.setId(1L);
        tariff.setName("tariff1");

        UnitOfMeasurement unitOfMeasurement = new UnitOfMeasurement();
        unitOfMeasurement.setId(1L);
        unitOfMeasurement.setName("unitOfMeasurement1");

        Service service1 = new Service();
        service1.setId(1L);
        service1.setName("service1");
        service1.setUnitOfMeasurement(unitOfMeasurement);
        Service service2 = new Service();
        service2.setId(1L);
        service2.setName("service2");
        service2.setUnitOfMeasurement(unitOfMeasurement);

        List<TariffItem> tariffItems = new ArrayList<>();
        TariffItem tariffItem = new TariffItem();
        tariffItem.setId(1L);
        tariffItem.setService(service1);
        tariffItem.setCurrency("currency");
        tariffItem.setServicePrice(BigDecimal.valueOf(50));

        TariffItem tariffItem2 = new TariffItem();
        tariffItem2.setId(2L);
        tariffItem2.setService(service2);
        tariffItem2.setCurrency("currency");
        tariffItem2.setServicePrice(BigDecimal.valueOf(100));

        tariffItems.add(tariffItem);
        tariffItems.add(tariffItem2);


        apartment = new Apartment();
        apartment.setId(1L);
        apartment.setTariff(tariff);

        List<TariffItemResponse> tariffItemResponses = new ArrayList<>();
        TariffItemResponse tariffItemResponse = new TariffItemResponse(
                tariffItem.getId(),
                tariffItem.getService().getName(),
                tariffItem.getService().getUnitOfMeasurement().getName(),
                tariffItem.getServicePrice());

        TariffItemResponse tariffItemResponse2 = new TariffItemResponse(
                tariffItem2.getId(),
                tariffItem2.getService().getName(),
                tariffItem2.getService().getUnitOfMeasurement().getName(),
                tariffItem2.getServicePrice());

        tariffItemResponses.add(tariffItemResponse);
        tariffItemResponses.add(tariffItemResponse2);

        tariffResponse = new TariffResponse(
                tariff.getId(),
                tariff.getName(),
                tariffItemResponses);
    }

    @Test
    void getApartmentTariff_IfApartmentExist_thenReturnTariff() {

        // when
        doReturn(Optional.of(apartment))
                .when(apartmentRepo).findById(apartment.getId());
        doReturn(tariffResponse)
                .when(tariffMapper).tariffToTariffResponse(tariff);
        TariffResponse actual = tariffServiceImpl.getApartmentTariff(apartment.getId());

        // then
        assertNotNull(actual);
        assertEquals(tariffResponse, actual);
    }

    @Test
    void getApartmentTariff_IfApartmentNotExist_thenThrowEntityNotFoundException() {

        // when
        doReturn(Optional.empty())
                .when(apartmentRepo).findById(apartment.getId());
        assertThrows(EntityNotFoundException.class,
                () -> tariffServiceImpl.getApartmentTariff(apartment.getId()));
    }
}