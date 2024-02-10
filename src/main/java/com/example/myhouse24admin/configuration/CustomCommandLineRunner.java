package com.example.myhouse24admin.configuration;

import com.example.myhouse24admin.service.ContactsPageService;
import com.example.myhouse24admin.service.RoleService;
import com.example.myhouse24admin.service.StaffService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CustomCommandLineRunner implements CommandLineRunner {
    private final RoleService roleService;
    private final StaffService staffService;
    private final ContactsPageService contactsPageService;

    public CustomCommandLineRunner(RoleService roleService, StaffService staffService, ContactsPageService contactsPageService) {
        this.roleService = roleService;
        this.staffService = staffService;
        this.contactsPageService = contactsPageService;
    }

    @Override
    public void run(String... args) throws Exception {
        roleService.createPermissions();
        staffService.createFirstStaff();
        contactsPageService.createContactsPageIfNotExist();
    }
}
