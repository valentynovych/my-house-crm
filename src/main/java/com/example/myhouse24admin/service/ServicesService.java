package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.services.ServiceDtoListWrap;
import com.example.myhouse24admin.model.services.ServiceResponse;

import java.util.List;

public interface ServicesService {

    List<ServiceResponse> getAllServices();

    void updateServices(ServiceDtoListWrap services);
}
