package com.example.myhouse24admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/houses")
public class HousesController {

    @GetMapping("add")
    public ModelAndView viewAddHouse() {
        return new ModelAndView("houses/add-house");
    }
    @GetMapping("edit-house/{houseId}")
    public ModelAndView viewAddHouse(@PathVariable Long houseId) {
        return new ModelAndView("houses/edit-house");
    }
}
