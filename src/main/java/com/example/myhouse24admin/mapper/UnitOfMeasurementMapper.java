package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.UnitOfMeasurement;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UnitOfMeasurementMapper {

    UnitOfMeasurement unitOfMeasurementDtoToUnitOfMeasurement(UnitOfMeasurementDto unitOfMeasurementDto);

    UnitOfMeasurementDto unitOfMeasurementToUnitOfMeasurementDto(UnitOfMeasurement unitOfMeasurement);

    List<UnitOfMeasurementDto> unitOfMeasurementListToUnitOfMeasurementDtoList(List<UnitOfMeasurement> unitOfMeasurements);

    List<UnitOfMeasurement> unitOfMeasurementListDtoToUnitOfMeasurementList(List<UnitOfMeasurementDto> unitOfMeasurementDtos);
}
