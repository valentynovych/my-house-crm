package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/web-site")
public class MainPageController {
    @GetMapping("")
    public ModelAndView getLoginPage() {
        return new ModelAndView("layout/layout");
    }
}
