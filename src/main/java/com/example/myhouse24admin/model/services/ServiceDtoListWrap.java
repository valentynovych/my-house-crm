package com.example.myhouse24admin.model.services;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

public class ServiceDtoListWrap {
    @Valid
    private List<ServiceDto> services = new ArrayList<>();
    private List<Long> serviceToDelete = new ArrayList<>();

    public ServiceDtoListWrap() {
    }

    public ServiceDtoListWrap(List<ServiceDto> services, List<Long> serviceToDelete) {
        this.services = services;
        this.serviceToDelete = serviceToDelete;
    }

    public List<ServiceDto> getServices() {
        return services;
    }

    public void setServices(List<ServiceDto> services) {
        this.services = services;
    }

    public List<Long> getServiceToDelete() {
        return serviceToDelete;
    }

    public void setServiceToDelete(List<Long> serviceToDelete) {
        this.serviceToDelete = serviceToDelete;
    }
}
