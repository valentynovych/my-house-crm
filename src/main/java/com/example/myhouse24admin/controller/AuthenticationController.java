package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.service.StaffService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthenticationController {
    private final StaffService staffService;

    public AuthenticationController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(){
        staffService.createFirstStaff();
        return new ModelAndView("security/login.html");
    }
}
