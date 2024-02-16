package com.example.myhouse24admin.controller.siteManagement;

import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageResponse;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageRequest;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageResponse;
import com.example.myhouse24admin.service.MainPageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/site-management/home-page")
public class MainPageController {
    private final MainPageService mainPageService;

    public MainPageController(MainPageService mainPageService) {
        this.mainPageService = mainPageService;
    }
    @GetMapping()
    public ModelAndView getMainPage() {
        return new ModelAndView("site-management/main-page");
    }
    @GetMapping("/get")
    public @ResponseBody MainPageResponse getMainPageResponse() {
        return mainPageService.getMainPageResponse();
    }
    @PostMapping()
    public ResponseEntity<?> updateMainPage(@ModelAttribute @Valid MainPageRequest mainPageRequest) {
        mainPageService.updateMainPage(mainPageRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
