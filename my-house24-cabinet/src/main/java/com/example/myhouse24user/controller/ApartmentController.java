package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.apartments.ApartmentShortResponse;
import com.example.myhouse24user.service.ApartmentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("cabinet/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("get-owner-apartments")
    public ResponseEntity<?> getOwnerApartments(Principal principal,
                                                @RequestParam("page") int page,
                                                @RequestParam("pageSize") int pageSize) {
        Page<ApartmentShortResponse> ownerApartments = apartmentService.getOwnerApartments(principal.getName(), page, pageSize);
        return new ResponseEntity<>(ownerApartments, HttpStatus.OK);
    }
}
