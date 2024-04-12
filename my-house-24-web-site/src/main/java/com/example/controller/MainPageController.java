package com.example.controller;

import com.example.model.mainPage.MainPageResponse;
import com.example.service.MainPageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainPageController {
    private final MainPageService mainPageService;

    public MainPageController(MainPageService mainPageService) {
        this.mainPageService = mainPageService;
    }

    @GetMapping("")
    public ModelAndView getMainPage() {
        return new ModelAndView("main/main");
    }
    @GetMapping("/get")
    public @ResponseBody MainPageResponse getMainPageResponse() {
        return mainPageService.getMainPageResponse();
    }
}
