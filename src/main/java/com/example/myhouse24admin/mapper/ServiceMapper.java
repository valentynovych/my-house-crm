package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.model.services.ServiceDto;
import com.example.myhouse24admin.model.services.ServiceResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {UnitOfMeasurementMapper.class})
public interface ServiceMapper {

    @Mapping(target = "unitOfMeasurementId", source = "unitOfMeasurement.id")
    ServiceDto serviceToServiceDto(Service service);

    @Mapping(target = "unitOfMeasurement.id", source = "unitOfMeasurementId")
    Service serviceToServiceDto(ServiceDto service);

    Service serviceToServiceResponse(ServiceResponse serviceResponse);

    ServiceResponse serviceResponseToService(Service service);

    List<ServiceDto> serviceListToServiceDtoList(List<Service> services);

    List<Service> serviceListDtoToServiceList(List<ServiceDto> services);

    List<ServiceResponse> serviceListToServiceResponseList(List<Service> services);

    List<Service> serviceResponseListToServiceList(List<ServiceResponse> services);
}
