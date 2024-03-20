package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.Language;
import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.model.authentication.RegistrationRequest;
import com.example.myhouse24user.model.owner.ApartmentOwnerRequest;
import com.example.myhouse24user.model.owner.ApartmentResponse;
import com.example.myhouse24user.model.owner.EditOwnerResponse;
import com.example.myhouse24user.model.owner.ViewOwnerResponse;
import com.example.myhouse24user.util.DateConverter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ApartmentOwnerMapper {

    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "ownerId", source = "newOwnerId")
    @Mapping(target = "creationDate", expression = "java(getToday())")
    @Mapping(target = "language", expression = "java(getLanguage())")
    @Mapping(target = "deleted", expression = "java(false)")
    @Mapping(target = "firstName", source = "registrationRequest.firstName")
    @Mapping(target = "lastName", source = "registrationRequest.lastName")
    @Mapping(target = "middleName", source = "registrationRequest.middleName")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "aboutOwner", source = "emptyValue")
    @Mapping(target = "phoneNumber", source = "emptyValue")
    @Mapping(target = "email", source = "registrationRequest.email")
    ApartmentOwner registrationRequestToApartmentOwner(RegistrationRequest registrationRequest,
                                                       String avatar, String encodedPassword,
                                                       OwnerStatus status, String newOwnerId,
                                                       String emptyValue);
    default Instant getToday() {
        return Instant.now();
    }
    default Language getLanguage() {
        return Language.UKR;
    }
    @Mapping(target = "apartmentResponses", source = "apartmentResponses")
    @Mapping(target = "id", source = "apartmentOwner.id")
    @Mapping(target = "firstName", source = "apartmentOwner.firstName")
    @Mapping(target = "lastName", source = "apartmentOwner.lastName")
    @Mapping(target = "middleName", source = "apartmentOwner.middleName")
    @Mapping(target = "phoneNumber", source = "apartmentOwner.phoneNumber")
    @Mapping(target = "viberNumber", source = "apartmentOwner.viberNumber")
    @Mapping(target = "telegramUsername", source = "apartmentOwner.telegramUsername")
    @Mapping(target = "email", source = "apartmentOwner.email")
    @Mapping(target = "avatar", source = "apartmentOwner.avatar")
    ViewOwnerResponse ownerToViewOwnerResponse(ApartmentOwner apartmentOwner,
                                               List<ApartmentResponse> apartmentResponses);
    @Mapping(target = "image", source = "avatar")
    @Mapping(target = "birthDate", expression = "java(convertDateToString(apartmentOwner.getBirthDate()))")
    EditOwnerResponse ownerToEditOwnerResponse(ApartmentOwner apartmentOwner);
    default String convertDateToString(Instant date) {
        return DateConverter.instantToString(date);
    }
    @Mapping(ignore = true, target = "password")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "ownerId")
    @Mapping(target = "birthDate", expression = "java(convertDateToInstant(apartmentOwnerRequest.birthDate()))")
    void setApartmentOwnerWithoutPassword(@MappingTarget ApartmentOwner apartmentOwner,
                                          ApartmentOwnerRequest apartmentOwnerRequest);
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(ignore = true, target = "ownerId")
    @Mapping(ignore = true, target = "id")
    @Mapping(target = "birthDate", expression = "java(convertDateToInstant(apartmentOwnerRequest.birthDate()))")
    void setApartmentOwnerWithPassword(@MappingTarget ApartmentOwner apartmentOwner,
                                       ApartmentOwnerRequest apartmentOwnerRequest,
                                       String encodedPassword);
    default Instant convertDateToInstant(String birthDate) {
        return DateConverter.stringToInstant(birthDate);
    }

}
