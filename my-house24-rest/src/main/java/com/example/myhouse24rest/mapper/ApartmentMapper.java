package com.example.myhouse24rest.mapper;

import com.example.myhouse24rest.entity.Apartment;
import com.example.myhouse24rest.model.apartment.ApartmentResponse;
import com.example.myhouse24rest.model.apartment.ApartmentShortResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ApartmentMapper {
    List<ApartmentShortResponse> apartmentListToApartmentShortResponseList(List<Apartment> apartmentList);

    @Mapping(target = "apartmentId", source = "apartment.id")
    @Mapping(target = "balance", source = "apartment.balance")
    @Mapping(target = "apartmentNumber", source = "apartment.apartmentNumber")
    ApartmentShortResponse apartmentToApartmentShortResponse(Apartment apartment);

    @Mapping(target = "apartmentId", source = "apartment.id")
    @Mapping(target = "apartmentNumber", source = "apartment.apartmentNumber")
    @Mapping(target = "personalAccountNumber", source = "apartment.personalAccount.accountNumber")
    @Mapping(target = "houseName", source = "apartment.house.name")
    @Mapping(target = "address", source = "apartment.house.address")
    @Mapping(target = "section", source = "apartment.section.name")
    @Mapping(target = "floor", source = "apartment.floor.name")
    ApartmentResponse apartmentToApartmentResponse(Apartment apartment);

    List<ApartmentResponse> apartmentListToApartmentResponseList(List<Apartment> apartments);
}
