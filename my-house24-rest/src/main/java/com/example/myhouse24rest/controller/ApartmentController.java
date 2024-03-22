package com.example.myhouse24rest.controller;

import com.example.myhouse24rest.model.apartment.ApartmentShortResponse;
import com.example.myhouse24rest.service.ApartmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/apartments")
@SecurityRequirement(name = "bearerAuth")
public class ApartmentController {


    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("get-all-apartments")
    public ResponseEntity<?> getAllApartments(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              Principal principal) {
        Page<ApartmentShortResponse> allApartments = apartmentService.getAllApartments(page, pageSize, principal);
        return new ResponseEntity<>(allApartments, HttpStatus.OK);
    }
}
