package com.example.myhouse24admin.service;

import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.model.meterReadings.ServiceNameResponse;
import com.example.myhouse24admin.model.services.ServiceDtoListWrap;
import com.example.myhouse24admin.model.services.ServiceResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ServicesService {

    List<ServiceResponse> getAllServices();

    void updateServices(ServiceDtoListWrap services);

    ServiceResponse getServiceById(Long serviceId);
    Page<ServiceNameResponse> getServicesForSelect(SelectSearchRequest selectSearchRequest);
}
