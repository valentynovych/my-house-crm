package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.houses.HouseAddRequest;
import com.example.myhouse24admin.service.HouseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/houses")
public class HousesController {

    private final HouseService houseService;

    public HousesController(HouseService houseService) {
        this.houseService = houseService;
    }

    @GetMapping("add")
    public ModelAndView viewAddHouse() {
        return new ModelAndView("houses/add-house");
    }

    @GetMapping("edit-house/{houseId}")
    public ModelAndView viewAddHouse(@PathVariable Long houseId) {

        return new ModelAndView("houses/edit-house");
    }

    @PostMapping("add")
    public @ResponseBody ResponseEntity<?> addNewHouse(@ModelAttribute @Valid HouseAddRequest houseAddRequest) {
        houseService.addNewHouse(houseAddRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
