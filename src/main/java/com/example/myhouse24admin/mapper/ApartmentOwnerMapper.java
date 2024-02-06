package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.Language;
import com.example.myhouse24admin.model.apartmentOwner.CreateApartmentOwnerRequest;
import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerResponse;
import com.example.myhouse24admin.model.apartmentOwner.EditApartmentOwnerRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface ApartmentOwnerMapper {
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "creationDate", expression = "java(getToday())")
    @Mapping(target = "birthDate", expression = "java(convertDate(createApartmentOwnerRequest.birthDate()))")
    @Mapping(target = "language", expression = "java(getLanguage())")
    @Mapping(target = "deleted", expression = "java(false)")
    @Mapping(target = "avatar", source = "avatar")
    ApartmentOwner apartmentOwnerRequestToApartmentOwner(CreateApartmentOwnerRequest createApartmentOwnerRequest, String encodedPassword, String avatar);
    default Instant getToday(){
        return Instant.now();
    }
    default Language getLanguage(){
        return Language.UKR;
    }
    default Instant convertDate(String birthDate){
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")), LocalTime.MIDNIGHT);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant();
    }
    @Mapping(target = "image", source = "avatar")
    ApartmentOwnerResponse apartmentOwnerToApartmentOwnerResponse(ApartmentOwner apartmentOwner);
    @Mapping(ignore = true, target = "password")
    void setApartmentOwnerWithoutPassword(@MappingTarget ApartmentOwner apartmentOwner, EditApartmentOwnerRequest apartmentOwnerRequest);
    @Mapping(target = "password", source = "encodedPassword")
    void setApartmentOwnerWithPassword(@MappingTarget ApartmentOwner apartmentOwner, EditApartmentOwnerRequest apartmentOwnerRequest, String encodedPassword);

}
