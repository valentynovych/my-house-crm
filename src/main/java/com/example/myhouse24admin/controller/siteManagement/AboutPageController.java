package com.example.myhouse24admin.controller.siteManagement;

import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageRequest;
import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageResponse;
import com.example.myhouse24admin.model.siteManagement.contacts.ContactsPageDto;
import com.example.myhouse24admin.service.AboutPageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/site-management/about-page")
public class AboutPageController {
    private final AboutPageService aboutPageService;

    public AboutPageController(AboutPageService aboutPageService) {
        this.aboutPageService = aboutPageService;
    }

    @GetMapping()
    public ModelAndView getAboutUsPage() {
        return new ModelAndView("site-management/about-page");
    }
    @GetMapping("/get")
    public @ResponseBody AboutPageResponse getAboutUs() {
        return aboutPageService.getAboutPageResponse();
    }
    @PostMapping()
    public ResponseEntity<?> updateAboutUsPage(@ModelAttribute @Valid AboutPageRequest aboutPageRequest) {
        aboutPageService.updateAboutPage(aboutPageRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
