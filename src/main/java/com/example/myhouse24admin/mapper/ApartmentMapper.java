package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ApartmentOwnerMapper.class,
                HouseMapper.class,
                SectionMapper.class,
                FloorMapper.class})
public interface ApartmentMapper {
    @Mapping(target = "owner.id", source = "ownerId")
    @Mapping(target = "house.id", source = "houseId")
    @Mapping(target = "section.id", source = "sectionId")
    @Mapping(target = "floor.id", source = "floorId")
    @Mapping(target = "tariff.id", source = "tariffId")
    @Mapping(target = "personalAccount.id", source = "personalAccountId")
    @Mapping(target = "balance", expression = "java(java.math.BigDecimal.ZERO)")
    Apartment apartmentAddRequestToApartment(ApartmentAddRequest apartmentAddRequest);

    List<ApartmentResponse> apartmentListToApartmentResponseList(List<Apartment> apartments);

    ApartmentResponse apartmentToApartmentResponse(Apartment apartments);
}
