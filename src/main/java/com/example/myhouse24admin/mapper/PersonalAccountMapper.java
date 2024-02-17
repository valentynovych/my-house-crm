package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.model.apartments.personaAccount.PersonalAccountShortResponse;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountTableResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
}
