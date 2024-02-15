package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.model.apartments.personaAccount.PersonalAccountShortResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PersonalAccountMapper {

    PersonalAccountShortResponse personalAccountToPersonalAccountShortResponse(PersonalAccount personalAccount);

    List<PersonalAccountShortResponse> personalAccountListToPersonalAccountShortResponseList(List<PersonalAccount> personalAccounts);
}
