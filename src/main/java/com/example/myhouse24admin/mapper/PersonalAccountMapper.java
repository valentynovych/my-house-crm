package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.model.personalAccounts.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PersonalAccountMapper {

    PersonalAccountShortResponse personalAccountToPersonalAccountShortResponse(PersonalAccount personalAccount);

    List<PersonalAccountShortResponse> personalAccountListToPersonalAccountShortResponseList(List<PersonalAccount> personalAccounts);

    List<PersonalAccountTableResponse> personalAccountListToPersonalAccountTableResponseList(List<PersonalAccount> personalAccounts);

    @Mapping(target = "apartment", source = "apartment")
    @Mapping(target = "apartment.owner.fullName", expression = "java(apartmentOwner.getLastName()+\" " +
            "\"+apartmentOwner.getMiddleName()+\" \"+apartmentOwner.getFirstName())")
    PersonalAccountTableResponse personalAccountToPersonalAccountTableResponse(PersonalAccount personalAccount);


    @Mapping(target = "apartment", source = "apartmentId", qualifiedByName = "setApartment")
    PersonalAccount personalAccountAddRequestToPersonalAccount(PersonalAccountAddRequest request);

    @Named(value = "setApartment")
    static Apartment setApartment(Long apartmentId) {
        if (apartmentId != null) {
            Apartment apartment = new Apartment();
            apartment.setId(apartmentId);
            return apartment;
        } else {
            return null;
        }
    }

    @Mapping(target = "apartment", source = "apartment")
    @Mapping(target = "apartment.owner.fullName", expression = "java(apartmentOwner.getLastName()+\" " +
            "\"+apartmentOwner.getMiddleName()+\" \"+apartmentOwner.getFirstName())")
    PersonalAccountResponse personalAccountToPersonalAccountResponse(PersonalAccount account);

    @Mapping(target = "apartment", source = "apartmentId", qualifiedByName = "setApartment")
    void updatePersonalAccountFromRequest(@MappingTarget PersonalAccount personalAccount, PersonalAccountUpdateRequest request);
}
