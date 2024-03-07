package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.exception.ServiceAlreadyUsedException;
import com.example.myhouse24admin.model.services.ServiceDtoListWrap;
import com.example.myhouse24admin.model.services.ServiceResponse;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDto;
import com.example.myhouse24admin.model.services.UnitOfMeasurementDtoListWrap;
import com.example.myhouse24admin.service.ServicesService;
import com.example.myhouse24admin.service.UnitOfMeasurementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
        try {
            unitOfMeasurementService.updateMeasurementUnist(units);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ServiceAlreadyUsedException usedException) {
            MultiValueMap<String, String> head = new LinkedMultiValueMap<>();
            head.add("Content-Type", "text/html; charset=utf-8");
            return new ResponseEntity<>(usedException.getServiceNames(), head, HttpStatus.CONFLICT);
        }
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

    @PostMapping(value = "update-services")
    public @ResponseBody ResponseEntity<?> updateServices(@ModelAttribute @Valid ServiceDtoListWrap servicesList) {
        try {
            servicesService.updateServices(servicesList);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ServiceAlreadyUsedException usedException) {
            MultiValueMap<String, String> head = new LinkedMultiValueMap<>();
            head.add("Content-Type", "text/html; charset=utf-8");
            return new ResponseEntity<>(usedException.getServiceNames(), head, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("get-service-by-id/{serviceId}")
    public ResponseEntity<ServiceResponse> getAllServices(@PathVariable @Min(1) Long serviceId) {
        ServiceResponse serviceResponse = servicesService.getServiceById(serviceId);
        return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
    }
}

