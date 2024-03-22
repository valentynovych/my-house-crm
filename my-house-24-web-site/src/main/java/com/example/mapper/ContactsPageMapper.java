package com.example.mapper;

import com.example.entity.ContactsPage;
import com.example.model.contactsPage.ContactsPageResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ContactsPageMapper {
    ContactsPageResponse contactsPageToContactsPageResponse(ContactsPage contactsPage);

}
