package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.entity.UnitOfMeasurement;
import com.example.myhouse24admin.exception.ServiceAlreadyUsedException;
import com.example.myhouse24admin.mapper.ServiceMapper;
import com.example.myhouse24admin.model.invoices.UnitNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.model.meterReadings.ServiceNameResponse;
import com.example.myhouse24admin.model.services.ServiceDto;
import com.example.myhouse24admin.model.services.ServiceDtoListWrap;
import com.example.myhouse24admin.model.services.ServiceResponse;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDto;
import com.example.myhouse24admin.repository.InvoiceItemRepo;
import com.example.myhouse24admin.repository.ServicesRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicesServiceImplTest {

    @Mock
    private ServicesRepo servicesRepo;
    @Mock
    private ServiceMapper mapper;
    @Mock
    private InvoiceItemRepo invoiceItemRepo;
    @InjectMocks
    private ServicesServiceImpl servicesService;
    private static List<Service> services;
    private static List<ServiceResponse> serviceResponses;
    @Captor
    private static ArgumentCaptor<List<Service>> serviceCaptor;

    @BeforeEach
    void setUp() {
        services = new ArrayList<>();
        serviceResponses = new ArrayList<>();
        UnitOfMeasurement unitOfMeasurement = new UnitOfMeasurement();
        unitOfMeasurement.setId(1L);
        unitOfMeasurement.setName("testUnit");
        unitOfMeasurement.setDeleted(false);

        UnitOfMeasurementDto unitOfMeasurementDto = new UnitOfMeasurementDto(
                unitOfMeasurement.getId(), unitOfMeasurement.getName());

        for (int i = 0; i < 7; i++) {
            Service service = new Service();
            service.setId((long) i);
            service.setName("Service_" + i);
            service.setShowInMeter(true);
            service.setUnitOfMeasurement(unitOfMeasurement);
            service.setDeleted(false);
            services.add(service);
            serviceResponses.add(new ServiceResponse(
                    service.getId(),
                    service.getName(),
                    service.isShowInMeter(),
                    unitOfMeasurementDto));
        }
    }

    @Test
    void getAllServices() {

        // when
        when(servicesRepo.findAll(any(Specification.class)))
                .thenReturn(services);
        when(mapper.serviceListToServiceResponseList(services))
                .thenReturn(serviceResponses);

        // then
        List<ServiceResponse> allServices = servicesService.getAllServices();
        assertFalse(allServices.isEmpty());
        assertEquals(7, allServices.size());
    }

    @Test
    void updateServices_WhenServiceToDeleteListIsEmpty() {
        // given
        ServiceDtoListWrap serviceDtoListWrap = new ServiceDtoListWrap();
        List<ServiceDto> serviceDtos = new ArrayList<>();
        for (Service service : services) {
            serviceDtos.add(new ServiceDto(
                    service.getId(),
                    service.getName(),
                    service.isShowInMeter(),
                    service.getUnitOfMeasurement().getId())
            );
        }

        serviceDtoListWrap.setServices(serviceDtos);

        // when
        when(mapper.serviceListDtoToServiceList(serviceDtos))
                .thenReturn(services);

        // then
        servicesService.updateServices(serviceDtoListWrap);
        verify(servicesRepo).saveAll(services);
    }

    @Test
    void updateServices_WhenServiceToDeleteListIsNotEmptyAndServiceIsUsedInInvoice() {
        // given
        ServiceDtoListWrap serviceDtoListWrap = new ServiceDtoListWrap();

        List<Long> longList = services.stream()
                .map(Service::getId)
                .toList();
        serviceDtoListWrap.setServiceToDelete(longList);

        // when
        when(invoiceItemRepo.existsInvoiceItemByService_Id(any(Long.class)))
                .thenReturn(true);
        when(servicesRepo.findAllById(eq(longList)))
                .thenReturn(services);

        // then
        assertThrows(ServiceAlreadyUsedException.class,
                () -> servicesService.updateServices(serviceDtoListWrap));
        verify(invoiceItemRepo, times(7)).existsInvoiceItemByService_Id(any(Long.class));
        verify(servicesRepo, times(1)).findAllById(eq(longList));
    }

    @Test
    void updateServices_WhenServiceToDeleteListIsNotEmptyAndServiceIsNotUsedInInvoice() {
        // given
        ServiceDtoListWrap serviceDtoListWrap = new ServiceDtoListWrap();

        List<Long> longList = services.stream()
                .map(Service::getId)
                .toList();
        serviceDtoListWrap.setServiceToDelete(longList);

        // when
        when(invoiceItemRepo.existsInvoiceItemByService_Id(any(Long.class)))
                .thenReturn(false);
        when(servicesRepo.findAllById(eq(longList)))
                .thenReturn(services);

        // then
        servicesService.updateServices(serviceDtoListWrap);
        verify(invoiceItemRepo, times(7)).existsInvoiceItemByService_Id(any(Long.class));
        verify(servicesRepo, times(1)).findAllById(eq(longList));
        verify(servicesRepo, times(1)).saveAll(serviceCaptor.capture());
        serviceCaptor.getValue().forEach(
                service -> assertTrue(service.isDeleted())
        );
    }

    @Test
    void getServiceById_WhenServiceNotFound() {
        // when
        when(servicesRepo.findById(1L))
                .thenReturn(Optional.empty());
        // then
        assertThrows(EntityNotFoundException.class,
                () -> servicesService.getServiceById(1L));
    }

    @Test
    void getServiceById_WhenServiceIsFound() {
        // given
        Service service = services.get(0);
        ServiceResponse serviceResponse = serviceResponses.get(0);

        // when
        when(servicesRepo.findById(1L))
                .thenReturn(Optional.of(service));
        when(mapper.serviceResponseToService(service))
                .thenReturn(serviceResponse);
        // then
        ServiceResponse serviceById = servicesService.getServiceById(1L);
        verify(servicesRepo).findById(1L);
        verify(mapper).serviceResponseToService(service);

        assertEquals(serviceResponse, serviceById);
    }

    @Test
    void getServicesForSelect() {
        // given
        SelectSearchRequest selectSearchRequest = new SelectSearchRequest("search", 1);
        Page<Service> servicesPage = new PageImpl<>(services, PageRequest.of(0, 7), 7);
        List<ServiceNameResponse> serviceNameResponses = new ArrayList<>();
        for (Service service : services) {
            serviceNameResponses.add(new ServiceNameResponse(
                    service.getId(),
                    service.getName()));
        }

        // when
        when(servicesRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(servicesPage);
        when(mapper.serviceListToServiceNameResponse(services))
                .thenReturn(serviceNameResponses);
        // then
        Page<ServiceNameResponse> servicesForSelect = servicesService.getServicesForSelect(selectSearchRequest);
        List<ServiceNameResponse> content = servicesForSelect.getContent();
        assertFalse(content.isEmpty());
        assertEquals(7, content.size());
    }

    @Test
    void getServicesForMeterReadingSelect() {
        // given
        SelectSearchRequest selectSearchRequest = new SelectSearchRequest("search", 1);
        Page<Service> servicesPage = new PageImpl<>(services, PageRequest.of(0, 7), 7);
        List<ServiceNameResponse> serviceNameResponses = new ArrayList<>();
        for (Service service : services) {
            serviceNameResponses.add(new ServiceNameResponse(
                    service.getId(),
                    service.getName()));
        }

        // when
        when(servicesRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(servicesPage);
        when(mapper.serviceListToServiceNameResponse(services))
                .thenReturn(serviceNameResponses);
        // then
        Page<ServiceNameResponse> servicesForSelect = servicesService.getServicesForMeterReadingSelect(selectSearchRequest);
        List<ServiceNameResponse> content = servicesForSelect.getContent();
        assertFalse(content.isEmpty());
        assertEquals(7, content.size());
    }

    @Test
    void getUnitOfMeasurementNameByServiceId() {
        // given
        Service service = services.get(0);
        String unitOfMeasurementName = service.getUnitOfMeasurement().getName();
        UnitNameResponse unitNameResponse = new UnitNameResponse(unitOfMeasurementName);

        // when
        when(servicesRepo.findById(1L))
                .thenReturn(Optional.of(service));

        // then
        UnitNameResponse unitOfMeasurementNameByServiceId = servicesService.getUnitOfMeasurementNameByServiceId(1L);
        assertEquals(unitNameResponse, unitOfMeasurementNameByServiceId);
    }
}