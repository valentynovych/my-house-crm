package com.example.serviceImpl;

import com.example.entity.ContactsPage;
import com.example.mapper.ContactsPageMapper;
import com.example.model.contactsPage.ContactsPageResponse;
import com.example.repository.ContactsPageRepo;
import com.example.service.ContactsService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ContactsServiceImpl implements ContactsService {
    private final ContactsPageRepo contactsPageRepo;
    private final ContactsPageMapper contactsPageMapper;
    private final Logger logger = LogManager.getLogger(ContactsServiceImpl.class);

    public ContactsServiceImpl(ContactsPageRepo contactsPageRepo, ContactsPageMapper contactsPageMapper) {
        this.contactsPageRepo = contactsPageRepo;
        this.contactsPageMapper = contactsPageMapper;
    }

    @Override
    public ContactsPageResponse getContactsPageResponse() {
        logger.info("getContactsPageResponse() - Getting contacts page response");
        ContactsPage contactsPage = contactsPageRepo.findById(1L).orElseThrow(()-> new EntityNotFoundException("Contacts page was not found by id 1"));
        ContactsPageResponse contactsPageResponse = contactsPageMapper.contactsPageToContactsPageResponse(contactsPage);
        logger.info("getContactsPageResponse() - Contacts page response was got");
        return contactsPageResponse;
    }
}
