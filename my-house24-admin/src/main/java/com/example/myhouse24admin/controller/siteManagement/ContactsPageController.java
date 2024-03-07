package com.example.myhouse24admin.controller.siteManagement;

import com.example.myhouse24admin.model.siteManagement.contacts.ContactsPageDto;
import com.example.myhouse24admin.service.ContactsPageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/site-management/contacts-page")
public class ContactsPageController {
    private final ContactsPageService contactsPageService;

    public ContactsPageController(ContactsPageService contactsPageService) {
        this.contactsPageService = contactsPageService;
    }

    @GetMapping()
    public ModelAndView getContactsPage() {
        return new ModelAndView("site-management/contacts");
    }
    @GetMapping("/get")
    public @ResponseBody ContactsPageDto getContacts() {
        return contactsPageService.getContactsPageDto();
    }
    @PostMapping()
    public ResponseEntity<?> updateContactsPage(@Valid @ModelAttribute ContactsPageDto contactsPageDto) {
        contactsPageService.updateContactsPage(contactsPageDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
