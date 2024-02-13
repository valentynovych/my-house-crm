package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.service.ApartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/apartments")
public class ApartmentsController {

    private final ApartmentService apartmentService;

    public ApartmentsController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping()
    public ModelAndView viewApartmentsTable() {
        return new ModelAndView("apartments/apartments");
    }

    @GetMapping("add")
    public ModelAndView viewAddApartments() {
        return new ModelAndView("apartments/add-apartment");
    }

    @PostMapping("add")
    public ResponseEntity<?> addNewApartment(@ModelAttribute @Valid ApartmentAddRequest apartmentAddRequest) {
        apartmentService.addNewApartment(apartmentAddRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
