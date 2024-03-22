package com.example.controller;

import com.example.model.aboutPage.AboutPageResponse;
import com.example.model.contactsPage.ContactsPageResponse;
import com.example.service.ContactsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/web-site/contacts")
public class ContactsController {
    private final ContactsService contactsService;

    public ContactsController(ContactsService contactsService) {
        this.contactsService = contactsService;
    }

    @GetMapping("")
    public ModelAndView getContactsPage() {
        return new ModelAndView("contacts/contacts");
    }
    @GetMapping("/get")
    public @ResponseBody ContactsPageResponse getContactsPageResponse() {
        return contactsService.getContactsPageResponse();
    }
}
