package com.example.myhouse24rest.mapper;

import com.example.myhouse24rest.entity.ApartmentOwner;
import com.example.myhouse24rest.model.profile.ProfileResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ApartmentMapper.class})
public interface ApartmentOwnerMapper {

    @Mapping(target = "myApartments", source = "apartmentOwner.apartments")
    @Mapping(target = "fullName", expression = "java(apartmentOwner.getFullName())")
    @Mapping(target = "profileId", source = "apartmentOwner.id")
    ProfileResponse apartmentOwnerToProfileResponse(ApartmentOwner apartmentOwner);
}
