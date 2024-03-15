package com.example.myhouse24user.mapper;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.Language;
import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.model.authentication.RegistrationRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

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
}
