package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.Language;
import com.example.myhouse24admin.model.apartmentOwner.*;
import com.example.myhouse24admin.model.invoices.OwnerResponse;
import com.example.myhouse24admin.util.DateConverter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface ApartmentOwnerMapper {
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "ownerId", source = "newOwnerId")
    @Mapping(target = "creationDate", expression = "java(getToday())")
    @Mapping(target = "birthDate", expression = "java(convertDateToInstant(createApartmentOwnerRequest.birthDate()))")
    @Mapping(target = "language", expression = "java(getLanguage())")
    @Mapping(target = "deleted", expression = "java(false)")
    @Mapping(target = "avatar", source = "avatar")
    ApartmentOwner apartmentOwnerRequestToApartmentOwner(CreateApartmentOwnerRequest createApartmentOwnerRequest,
                                                         String encodedPassword,
                                                         String avatar,
                                                         String newOwnerId);

    default Instant getToday() {
        return Instant.now();
    }

    default Language getLanguage() {
        return Language.UKR;
    }

    @Mapping(target = "image", source = "avatar")
    @Mapping(target = "birthDate", expression = "java(convertDateToString(apartmentOwner.getBirthDate()))")
    ApartmentOwnerResponse apartmentOwnerToApartmentOwnerResponse(ApartmentOwner apartmentOwner);

    @Mapping(ignore = true, target = "password")
    @Mapping(ignore = true, target = "ownerId")
    @Mapping(target = "birthDate", expression = "java(convertDateToInstant(editApartmentOwnerRequest.birthDate()))")
    void setApartmentOwnerWithoutPassword(@MappingTarget ApartmentOwner apartmentOwner, EditApartmentOwnerRequest editApartmentOwnerRequest);

    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(ignore = true, target = "ownerId")
    @Mapping(target = "birthDate", expression = "java(convertDateToInstant(editApartmentOwnerRequest.birthDate()))")
    void setApartmentOwnerWithPassword(@MappingTarget ApartmentOwner apartmentOwner, EditApartmentOwnerRequest editApartmentOwnerRequest, String encodedPassword);

    List<TableApartmentOwnerResponse> apartmentOwnerListToTableApartmentOwnerResponseList(List<ApartmentOwner> apartmentOwners);

    @Mapping(target = "fullName", expression = "java(apartmentOwner.getLastName()+\" \"+apartmentOwner.getMiddleName()+\" \"+apartmentOwner.getFirstName())")
    @Mapping(target = "creationDate", expression = "java(convertDateToString(apartmentOwner.getCreationDate()))")
    TableApartmentOwnerResponse apartmentOwnerToTableApartmentOwnerResponse(ApartmentOwner apartmentOwner);

    @Mapping(target = "birthDate", expression = "java(convertDateToString(apartmentOwner.getBirthDate()))")
    ViewApartmentOwnerResponse apartmentOwnerToViewApartmentOwnerResponse(ApartmentOwner apartmentOwner);

    default String convertDateToString(Instant date) {
        return DateConverter.instantToString(date);
    }

    default Instant convertDateToInstant(String birthDate) {
        return DateConverter.stringToInstant(birthDate);
    }

    List<ApartmentOwnerShortResponse> apartmentOwnerListToTApartmentOwnerShortResponseList(List<ApartmentOwner> apartmentOwners);
    @Mapping(target = "fullName", expression = "java(apartmentOwner.getLastName()+\" \"+apartmentOwner.getMiddleName()+\" \"+apartmentOwner.getFirstName())")
    ApartmentOwnerShortResponse apartmentOwnerToTApartmentOwnerShortResponse(ApartmentOwner apartmentOwner);
    @Mapping(target = "ownerFullName", expression = "java(apartment.getOwner().getLastName()+\" \"+apartment.getOwner().getMiddleName()+\" \"+apartment.getOwner().getFirstName())")
    @Mapping(target = "ownerPhoneNumber", source = "apartment.owner.phoneNumber")
    @Mapping(target = "accountNumber", source = "apartment.personalAccount.accountNumber")
    @Mapping(target = "tariffId", source = "apartment.tariff.id")
    @Mapping(target = "tariffName", source = "apartment.tariff.name")
    OwnerResponse apartmentToOwnerResponse(Apartment apartment);
}
