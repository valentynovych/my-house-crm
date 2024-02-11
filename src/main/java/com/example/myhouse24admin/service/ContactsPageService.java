package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.siteManagement.contacts.ContactsPageDto;

public interface ContactsPageService {
    ContactsPageDto getContactsPageDto();
    void createContactsPageIfNotExist();
    void updateContactsPage(ContactsPageDto contactsPageDto);
}
