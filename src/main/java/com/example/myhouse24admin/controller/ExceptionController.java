package com.example.myhouse24admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ExceptionController {
    @GetMapping("/access-denied")
    public ModelAndView getAccessDenied() {
        return new ModelAndView("errorPage/403-error");
    }
}
