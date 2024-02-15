package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.model.apartments.ApartmentExtendResponse;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import com.example.myhouse24admin.service.ApartmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

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
    public ModelAndView viewAddApartment() {
        return new ModelAndView("apartments/add-apartment");
    }

    @GetMapping("edit-apartment/{apartmentId}")
    public ModelAndView viewEditApartment(@PathVariable Long apartmentId) {
        return new ModelAndView("apartments/edit-apartment");
    }

    @PostMapping("add")
    public ResponseEntity<?> addNewApartment(@ModelAttribute @Valid ApartmentAddRequest apartmentAddRequest) {
        apartmentService.addNewApartment(apartmentAddRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-apartments")
    public @ResponseBody ResponseEntity<?> getApartments(@RequestParam int page,
                                                         @RequestParam int pageSize,
                                                         @RequestParam Map<String, String> searchParams){
        Page<ApartmentResponse> apartmentResponses = apartmentService.getApartments(page, pageSize, searchParams);
        return new ResponseEntity<>(apartmentResponses, HttpStatus.OK);
    }

    @GetMapping("get-apartment/{apartmentId}")
    public @ResponseBody ResponseEntity<?> getApartmentById(@PathVariable Long apartmentId){
        ApartmentExtendResponse apartmentResponse = apartmentService.getApartmentById(apartmentId);
        return new ResponseEntity<>(apartmentResponse, HttpStatus.OK);
    }

    @PostMapping("edit-apartment/{apartmentId}")
    public ResponseEntity<?> updateApartment(@PathVariable Long apartmentId,
                                             @ModelAttribute @Valid ApartmentAddRequest apartmentRequest) {
        apartmentService.updateApartment(apartmentId, apartmentRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
