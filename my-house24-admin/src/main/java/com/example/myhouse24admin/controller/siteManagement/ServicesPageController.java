package com.example.myhouse24admin.controller.siteManagement;

import com.example.myhouse24admin.entity.ServicePageBlock;
import com.example.myhouse24admin.model.siteManagement.contacts.ContactsPageDto;
import com.example.myhouse24admin.model.siteManagement.servicesPage.SeoRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicePageBlockRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicePageRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicesPageResponse;
import com.example.myhouse24admin.service.ServicesPageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin/site-management/service-page")
public class ServicesPageController {
    private final ServicesPageService servicesPageService;

    public ServicesPageController(ServicesPageService servicesPageService) {
        this.servicesPageService = servicesPageService;
    }

    @GetMapping()
    public ModelAndView getServicesPage() {
        return new ModelAndView("site-management/services-page");
    }
    @GetMapping("/get")
    public @ResponseBody ServicesPageResponse getServicesPageResponse() {
        return servicesPageService.getServicesPageResponse();
    }
    @PostMapping()
    public @ResponseBody ResponseEntity<?> updateServicesPage(@ModelAttribute @Valid ServicePageRequest servicePageRequest) {
        servicesPageService.updateServicesPage(servicePageRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
