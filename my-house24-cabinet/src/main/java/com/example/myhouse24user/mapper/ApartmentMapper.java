package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.model.owner.ApartmentResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ApartmentMapper {
    List<ApartmentResponse> apartmentListToApartmentResponseList(List<Apartment> apartments);
    @Mapping(target = "houseName", source = "house.name")
    @Mapping(target = "houseAddress", source = "house.address")
    @Mapping(target = "floor", source = "floor.name")
    @Mapping(target = "section", source = "section.name")
    @Mapping(target = "personalAccount", source = "personalAccount.accountNumber")
    @Mapping(target = "image1", source = "house.image1")
    @Mapping(target = "image2", source = "house.image2")
    @Mapping(target = "image3", source = "house.image3")
    @Mapping(target = "image4", source = "house.image4")
    @Mapping(target = "image5", source = "house.image5")
    ApartmentResponse apartmentToApartmentResponse(Apartment apartment);

}
