package com.example.myhouse24admin.configuration;

import com.example.myhouse24admin.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CustomCommandLineRunner implements CommandLineRunner {
    private final RoleService roleService;
    private final StaffService staffService;
    private final ContactsPageService contactsPageService;
    private final ServicesPageService servicesPageService;
    private final AboutPageService aboutPageService;

    public CustomCommandLineRunner(RoleService roleService,
                                   StaffService staffService,
                                   ContactsPageService contactsPageService,
                                   ServicesPageService servicesPageService,
                                   AboutPageService aboutPageService) {
        this.roleService = roleService;
        this.staffService = staffService;
        this.contactsPageService = contactsPageService;
        this.servicesPageService = servicesPageService;
        this.aboutPageService = aboutPageService;
    }

    @Override
    public void run(String... args) throws Exception {
        roleService.createPermissions();
        staffService.createFirstStaff();
        contactsPageService.createContactsPageIfNotExist();
        servicesPageService.createServicesPageIfNotExist();
        aboutPageService.createAboutPageIfNotExist();
    }
}
