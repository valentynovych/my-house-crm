package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.mapper.ServiceMapper;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.model.meterReadings.ServiceNameResponse;
import com.example.myhouse24admin.model.services.ServiceDtoListWrap;
import com.example.myhouse24admin.model.services.ServiceResponse;
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

import java.util.List;
import java.util.Optional;

import static com.example.myhouse24admin.specification.ServiceSpecification.byDeleted;
import static com.example.myhouse24admin.specification.ServiceSpecification.byNameLike;

@Component
public class ServicesServiceImpl implements ServicesService {

    private final ServicesRepo servicesRepo;
    private final ServiceMapper mapper;
    private final Logger logger = LogManager.getLogger(ServicesServiceImpl.class);

    public ServicesServiceImpl(ServicesRepo servicesRepo, ServiceMapper mapper) {
        this.servicesRepo = servicesRepo;
        this.mapper = mapper;
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

            if (services.getServiceToDelete() != null
                    && !services.getServiceToDelete().isEmpty()) {
                //TODO add throw exception when service already use in invoices
                List<Long> serviceToDelete = services.getServiceToDelete();
                logger.info("updateServices() -> request have service to delete, by ids: {}",
                        serviceToDelete.toArray());
                List<Service> allById = servicesRepo.findAllById(serviceToDelete);
                allById.forEach(service -> service.setDeleted(true));
                servicesRepo.saveAll(allById);
                logger.info("updateServices() -> success mark all service \"delete\", by ids: {}",
                        serviceToDelete.toArray());

            }
            logger.info("updateServices() -> exit, success update service list");
        }
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
        Pageable pageable = PageRequest.of(selectSearchRequest.page()-1, 10);
        Page<Service> services = getFilteredServicesForSelect(selectSearchRequest.search(), pageable);
        List<ServiceNameResponse> serviceNameResponses = mapper.serviceListToServiceNameResponse(services.getContent());
        Page<ServiceNameResponse> serviceNameResponsePage = new PageImpl<>(serviceNameResponses, pageable, services.getTotalElements());
        logger.info("getServicesForSelect - Services name responses were got");
        return serviceNameResponsePage;
    }

    private Page<Service> getFilteredServicesForSelect(String search, Pageable pageable) {
        Specification<Service> serviceSpecification = Specification.where(byDeleted());
        if(!search.isEmpty()){
            serviceSpecification = serviceSpecification.and(byNameLike(search));
        }
        return servicesRepo.findAll(serviceSpecification, pageable);
    }
}
