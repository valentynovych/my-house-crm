package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.exception.ServiceAlreadyUsedException;
import com.example.myhouse24admin.mapper.ServiceMapper;
import com.example.myhouse24admin.model.invoices.UnitNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.model.meterReadings.ServiceNameResponse;
import com.example.myhouse24admin.model.services.ServiceDtoListWrap;
import com.example.myhouse24admin.model.services.ServiceResponse;
import com.example.myhouse24admin.repository.InvoiceItemRepo;
import com.example.myhouse24admin.repository.ServicesRepo;
import com.example.myhouse24admin.service.ServicesService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.myhouse24admin.specification.ServiceSpecification.*;

@Component
public class ServicesServiceImpl implements ServicesService {

    private final ServicesRepo servicesRepo;
    private final ServiceMapper mapper;
    private final InvoiceItemRepo invoiceItemRepo;
    private final Logger logger = LogManager.getLogger(ServicesServiceImpl.class);

    public ServicesServiceImpl(ServicesRepo servicesRepo, ServiceMapper mapper, InvoiceItemRepo invoiceItemRepo) {
        this.servicesRepo = servicesRepo;
        this.mapper = mapper;
        this.invoiceItemRepo = invoiceItemRepo;
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        logger.info("getAllServices() -> start");
        List<Service> services = servicesRepo.findAll(Specification.where(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false)));
        List<ServiceResponse> serviceResponses = mapper.serviceListToServiceResponseList(services);
        logger.info("getAllServices() -> exit, return list size: {}", serviceResponses.size());
        return serviceResponses;
    }

    @Override
    public void updateServices(ServiceDtoListWrap services) {
        logger.info("updateServices() -> start");
        if (services.getServices() != null
                && !services.getServices().isEmpty()) {
            List<Service> services1 = mapper.serviceListDtoToServiceList(services.getServices());
            logger.info("updateServices() -> start save list service of size: {}", services1.size());
            servicesRepo.saveAll(services1);
            logger.info("updateServices() -> success save service list");
        }

        if (services.getServiceToDelete() != null
                && !services.getServiceToDelete().isEmpty()) {
            List<Long> serviceToDelete = services.getServiceToDelete();
            logger.info("updateServices() -> request have service to delete, by ids: {}",
                    serviceToDelete);
            deleteServicesByIds(serviceToDelete);
            logger.info("updateServices() -> success mark all service \"delete\", by ids: {}",
                    serviceToDelete.toArray());
        }
        logger.info("updateServices() -> exit, success update service list");
    }

    private void deleteServicesByIds(List<Long> servicesIds) {
        List<Long> longs = checkUsedServiceInInvoices(servicesIds);
        if (!longs.isEmpty()) {
            String servicesNameByIds = getServicesNameByIds(longs);
            throw new ServiceAlreadyUsedException(
                    String.format("Services: %s used in invoices, can`t delete them", longs),
                    servicesNameByIds);
        }
        List<Service> allById = servicesRepo.findAllById(servicesIds);
        allById.forEach(service -> service.setDeleted(true));
        servicesRepo.saveAll(allById);
    }

    private String getServicesNameByIds(List<Long> servicesIds) {
        List<Service> allById = servicesRepo.findAllById(servicesIds);
        String collect = allById.stream()
                .map(Service::getName)
                .collect(Collectors.joining(", "));
        return collect;
    }

    private List<Long> checkUsedServiceInInvoices(List<Long> serviceIds) {
        List<Long> usedServices = new ArrayList<>();
        for (Long serviceId : serviceIds) {
            if (invoiceItemRepo.existsInvoiceItemByService_Id(serviceId)) {
                usedServices.add(serviceId);
            }
        }
        return usedServices;
    }

    @Override
    public ServiceResponse getServiceById(Long serviceId) {
        logger.info("getServiceById() -> start with id: {}", serviceId);
        Optional<Service> byId = servicesRepo.findById(serviceId);
        Service service = byId.orElseThrow(() ->
                new EntityNotFoundException(String.format("Service with id: %s not found", serviceId)));
        ServiceResponse serviceResponse = mapper.serviceResponseToService(service);
        logger.info("getServiceById() -> exit, service with id: {} was found", serviceId);
        return serviceResponse;
    }

    @Override
    public Page<ServiceNameResponse> getServicesForSelect(SelectSearchRequest selectSearchRequest) {
        logger.info("getServicesForSelect - Getting services name responses for select, " + selectSearchRequest.toString());
        Pageable pageable = PageRequest.of(selectSearchRequest.page() - 1, 10);
        Page<Service> services = servicesRepo.findAll(getServicesSpecification(selectSearchRequest.search()), pageable);
        List<ServiceNameResponse> serviceNameResponses = mapper.serviceListToServiceNameResponse(services.getContent());
        Page<ServiceNameResponse> serviceNameResponsePage = new PageImpl<>(serviceNameResponses, pageable, services.getTotalElements());
        logger.info("getServicesForSelect - Services name responses were got");
        return serviceNameResponsePage;
    }

    @Override
    public Page<ServiceNameResponse> getServicesForMeterReadingSelect(SelectSearchRequest selectSearchRequest) {
        logger.info("getServicesForMeterSelect - Getting services name responses for meter reading select, " + selectSearchRequest.toString());
        Pageable pageable = PageRequest.of(selectSearchRequest.page() - 1, 10);
        Page<Service> services = servicesRepo.findAll(getServicesSpecification(selectSearchRequest.search())
                .and(byShowInMeter()), pageable);
        List<ServiceNameResponse> serviceNameResponses = mapper.serviceListToServiceNameResponse(services.getContent());
        Page<ServiceNameResponse> serviceNameResponsePage = new PageImpl<>(serviceNameResponses, pageable, services.getTotalElements());
        logger.info("getServicesForMeterSelect - Services name responses were got");
        return serviceNameResponsePage;
    }

    private Specification<Service> getServicesSpecification(String search) {
        Specification<Service> serviceSpecification = Specification.where(byDeleted());
        if (!search.isEmpty()) {
            serviceSpecification = serviceSpecification.and(byNameLike(search));
        }
        return serviceSpecification;
    }

    @Override
    public UnitNameResponse getUnitOfMeasurementNameByServiceId(Long serviceId) {
        logger.info("getUnitOfMeasurementNameByServiceId - Getting unit of measurement name by service id " + serviceId);
        Service service = servicesRepo.findById(serviceId).orElseThrow(() -> new EntityNotFoundException("Service was not found by id " + serviceId));
        String unitOfMeasurementName = service.getUnitOfMeasurement().getName();
        logger.info("getUnitOfMeasurementNameByServiceId - Unit of measurement name was got");
        return new UnitNameResponse(unitOfMeasurementName);
    }
}
