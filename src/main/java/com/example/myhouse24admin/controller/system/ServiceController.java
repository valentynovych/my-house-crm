package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.model.services.*;
import com.example.myhouse24admin.service.ServicesService;
import com.example.myhouse24admin.service.UnitOfMeasurementService;
import jakarta.validation.Valid;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("admin/system-settings/services")
public class ServiceController {

    private final UnitOfMeasurementService unitOfMeasurementService;
    private final ServicesService servicesService;

    public ServiceController(UnitOfMeasurementService unitOfMeasurementService, ServicesService servicesService) {
        this.unitOfMeasurementService = unitOfMeasurementService;
        this.servicesService = servicesService;
    }


    @GetMapping()
    public ModelAndView viewServices() {
        return new ModelAndView("system/services/services");
    }

    @PostMapping("update-measurement-unist")
    public ResponseEntity<?> updateMeasurementUnits(@ModelAttribute @Valid UnitOfMeasurementDtoListWrap units) {
        unitOfMeasurementService.updateMeasurementUnist(units);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-measurement-units")
    public ResponseEntity<List<UnitOfMeasurementDto>> getAllMeasurementUnits() {
        List<UnitOfMeasurementDto> allMeasurementUnits = unitOfMeasurementService.getAllMeasurementUnits();
        return new ResponseEntity<>(allMeasurementUnits, HttpStatus.OK);
    }

    @GetMapping("get-services")
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        List<ServiceResponse> serviceList = servicesService.getAllServices();
        return new ResponseEntity<>(serviceList, HttpStatus.OK);
    }

    @PostMapping("update-services")
    public ResponseEntity<?> updateServices(@ModelAttribute @Valid ServiceDtoListWrap servicesList) {
        servicesService.updateServices(servicesList);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}

