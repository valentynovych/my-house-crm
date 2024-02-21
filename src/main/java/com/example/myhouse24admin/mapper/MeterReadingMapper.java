package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.model.meterReadings.*;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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
    @Mapping(target = "houseNameResponse", expression = "java(createHouseNameResponse(meterReading.getApartment().getHouse()))")
    @Mapping(target = "sectionNameResponse", expression = "java(createSectionNameResponse(meterReading.getApartment().getSection()))")
    @Mapping(target = "apartmentNumberResponse", expression = "java(createApartmentNumberResponse(meterReading.getApartment()))")
    @Mapping(target = "serviceNameResponse", expression = "java(createServiceNameResponse(meterReading.getService()))")
    @Mapping(target = "creationDate", expression = "java(convertInstantToString(meterReading.getCreationDate()))")
    MeterReadingResponse meterReadingToMeterReadingResponse(MeterReading meterReading);
    default String convertInstantToString(Instant date) {
        LocalDate localDate = LocalDate.ofInstant(date, ZoneId.systemDefault());
        return localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    default HouseNameResponse createHouseNameResponse(House house){
        return new HouseNameResponse(house.getId(), house.getName());
    }
    default SectionNameResponse createSectionNameResponse(Section section){
        return new SectionNameResponse(section.getId(), section.getName());
    }
    default ApartmentNumberResponse createApartmentNumberResponse(Apartment apartment){
        return new ApartmentNumberResponse(apartment.getId(), apartment.getApartmentNumber());
    }
    default ServiceNameResponse createServiceNameResponse(Service service){
        return new ServiceNameResponse(service.getId(), service.getName());
    }
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "apartment", source = "apartment")
    @Mapping(target = "service", source = "service")
    void updateMeterReading(@MappingTarget MeterReading meterReading, MeterReadingRequest meterReadingRequest, Apartment apartment, Service service);
}
