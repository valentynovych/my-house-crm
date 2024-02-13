package com.example.myhouse24admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/apartments")
public class ApartmentsController {

    @GetMapping()
    public ModelAndView viewApartmentsTable() {
        return new ModelAndView("apartments/apartments");
    }

    @GetMapping("add")
    public ModelAndView viewAddApartments() {
        return new ModelAndView("apartments/add-apartment");
    }
}
