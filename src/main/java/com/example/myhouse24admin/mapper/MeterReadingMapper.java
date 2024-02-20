package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.MeterReading;
import com.example.myhouse24admin.entity.Service;
import com.example.myhouse24admin.model.meterReadings.MeterReadingRequest;
import com.example.myhouse24admin.model.meterReadings.TableMeterReadingResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MeterReadingMapper {
    @Mapping(target = "creationDate", expression = "java(convertStringToInstant(meterReadingRequest.creationDate()))")
    @Mapping(target = "apartment", source = "apartment")
    @Mapping(target = "service", source = "service")
    @Mapping(target = "number", source = "number")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "deleted")
    MeterReading meterReadingRequestToMeterReading(MeterReadingRequest meterReadingRequest,
                                                   Apartment apartment, Service service,
                                                   String number);
    default Instant convertStringToInstant(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    List<TableMeterReadingResponse> meterReadingListToTableMeterReadingResponseList(List<MeterReading> meterReadings);
    @Mapping(target = "apartmentId", source = "apartment.id")
    @Mapping(target = "houseName", source = "apartment.house.name")
    @Mapping(target = "sectionName", source = "apartment.section.name")
    @Mapping(target = "apartmentName", expression = "java(meterReading.getApartment().getApartmentNumber()+\", \"+meterReading.getApartment().getHouse().getName())")
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "measurementName", source = "service.unitOfMeasurement.name")
    TableMeterReadingResponse meterReadingToTableMeterReadingResponse(MeterReading meterReading);
}
