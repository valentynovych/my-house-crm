package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.Language;
import com.example.myhouse24admin.model.apartmentOwner.CreateApartmentOwnerRequest;
import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerResponse;
import com.example.myhouse24admin.model.apartmentOwner.EditApartmentOwnerRequest;
import com.example.myhouse24admin.model.apartmentOwner.TableApartmentOwnerResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface ApartmentOwnerMapper {
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "creationDate", expression = "java(getToday())")
    @Mapping(target = "birthDate", expression = "java(convertDateToInstant(createApartmentOwnerRequest.birthDate()))")
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
    default Instant convertDateToInstant(String birthDate){
        LocalDate date = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
    @Mapping(target = "image", source = "avatar")
    @Mapping(target = "birthDate", expression = "java(convertDateToString(apartmentOwner.getBirthDate()))")
    ApartmentOwnerResponse apartmentOwnerToApartmentOwnerResponse(ApartmentOwner apartmentOwner);
    @Mapping(ignore = true, target = "password")
    @Mapping(target = "birthDate", expression = "java(convertDateToInstant(editApartmentOwnerRequest.birthDate()))")
    void setApartmentOwnerWithoutPassword(@MappingTarget ApartmentOwner apartmentOwner, EditApartmentOwnerRequest editApartmentOwnerRequest);
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "birthDate", expression = "java(convertDateToInstant(editApartmentOwnerRequest.birthDate()))")
    void setApartmentOwnerWithPassword(@MappingTarget ApartmentOwner apartmentOwner, EditApartmentOwnerRequest editApartmentOwnerRequest, String encodedPassword);
    List<TableApartmentOwnerResponse> apartmentOwnerListToTableApartmentOwnerResponseList(List<ApartmentOwner> apartmentOwners);
    @Mapping(target = "fullName", expression = "java(apartmentOwner.getLastName()+\" \"+apartmentOwner.getMiddleName()+\" \"+apartmentOwner.getFirstName())")
    @Mapping(target = "creationDate", expression = "java(convertDateToString(apartmentOwner.getCreationDate()))")
    TableApartmentOwnerResponse apartmentOwnerToTableApartmentOwnerResponse(ApartmentOwner apartmentOwner);
    default String convertDateToString(Instant date){
        LocalDate localDate = LocalDate.ofInstant(date, ZoneId.systemDefault());
        return localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
