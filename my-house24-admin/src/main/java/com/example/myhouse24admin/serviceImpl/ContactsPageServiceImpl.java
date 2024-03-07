package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.ContactsPage;
import com.example.myhouse24admin.entity.Seo;
import com.example.myhouse24admin.mapper.ContactsPageMapper;
import com.example.myhouse24admin.model.siteManagement.contacts.ContactsPageDto;
import com.example.myhouse24admin.repository.ContactsPageRepo;
import com.example.myhouse24admin.repository.SeoRepo;
import com.example.myhouse24admin.service.ContactsPageService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ContactsPageServiceImpl implements ContactsPageService {
    private final ContactsPageRepo contactsPageRepo;
    private final ContactsPageMapper contactsPageMapper;
    private final SeoRepo seoRepo;
    private final Logger logger = LogManager.getLogger(ApartmentOwnerServiceImpl.class);

    public ContactsPageServiceImpl(ContactsPageRepo contactsPageRepo, ContactsPageMapper contactsPageMapper, SeoRepo seoRepo) {
        this.contactsPageRepo = contactsPageRepo;
        this.contactsPageMapper = contactsPageMapper;
        this.seoRepo = seoRepo;
    }

    @Override
    public ContactsPageDto getContactsPageDto() {
        logger.info("getContactsPageDto - Getting contacts page dto");
        ContactsPage contactsPage = contactsPageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("Contacts page not found"));
        ContactsPageDto contactsPageDto = contactsPageMapper.contactsPageToContactsPageResponse(contactsPage);
        logger.info("getContactsPageDto - Contacts page dto was got");
        return contactsPageDto;
    }

    @Override
    public void updateContactsPage(ContactsPageDto contactsPageDto) {
        logger.info("updateContactsPage - Updating contacts page");
        ContactsPage contactsPage = contactsPageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("Contacts page not found"));
        contactsPageMapper.setContactsPage(contactsPage, contactsPageDto);
        contactsPageRepo.save(contactsPage);
        logger.info("getContactsPageDto - Contacts page was updated");
    }

    @Override
    public void createContactsPageIfNotExist() {
        logger.info("createContactsPageIfNotExist - Creating contacts page if it doesn't exist");
        if(isTableEmpty()) {
            Seo seo = new Seo();
            ContactsPage contactsPage = contactsPageMapper.createContactsPage("", seo);
            contactsPageRepo.save(contactsPage);
            logger.info("createContactsPageIfNotExist - Contacts page was created");
        } else {
            logger.info("createContactsPageIfNotExist - Contacts page has already been created");
        }
    }
    boolean isTableEmpty(){
        return contactsPageRepo.count() == 0;
    }
}
