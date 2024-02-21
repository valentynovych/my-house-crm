package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.MeterReadingStatus;
import com.example.myhouse24admin.model.meterReadings.*;
import com.example.myhouse24admin.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/meter-readings")
public class MeterReadingController {
    private final HouseService houseService;
    private final SectionService sectionService;
    private final ApartmentService apartmentService;
    private final ServicesService servicesService;
    private final MeterReadingService meterReadingService;

    public MeterReadingController(HouseService houseService,
                                  SectionService sectionService,
                                  ApartmentService apartmentService,
                                  ServicesService servicesService,
                                  MeterReadingService meterReadingService) {
        this.houseService = houseService;
        this.sectionService = sectionService;
        this.apartmentService = apartmentService;
        this.servicesService = servicesService;
        this.meterReadingService = meterReadingService;
    }

    @GetMapping()
    public ModelAndView getMeterReadingsPage() {
        return new ModelAndView("meter-readings/meter-readings");
    }
    @GetMapping("/get")
    public @ResponseBody Page<TableMeterReadingResponse> getMeterReadings(@RequestParam(name = "page") int page,
                                                                          @RequestParam(name = "pageSize") int pageSize,
                                                                          FilterRequest filterRequest) {
        return meterReadingService.getMeterReadingResponsesForTable(page, pageSize, filterRequest);
    }

    @GetMapping("/add")
    public ModelAndView getMeterReadingPageForCreate() {
        ModelAndView modelAndView = new ModelAndView("meter-readings/meter-reading");
        modelAndView.addObject("statusLink", "get-statuses");
        modelAndView.addObject("houseLink", "get-houses");
        modelAndView.addObject("sectionLink", "get-sections");
        modelAndView.addObject("apartmentLink", "get-apartments");
        modelAndView.addObject("serviceLink", "get-services");
        return modelAndView;
    }
    @GetMapping("/get-statuses")
    public @ResponseBody MeterReadingStatus[] getStatuses() {
        return MeterReadingStatus.values();
    }
    @GetMapping("/get-houses")
    public @ResponseBody Page<HouseNameResponse> getHouses(SelectSearchRequest selectSearchRequest) {
        return houseService.getHousesForSelect(selectSearchRequest);
    }
    @GetMapping("/get-sections")
    public @ResponseBody Page<SectionNameResponse> getSections(SelectSearchRequest selectSearchRequest,
                                                               @RequestParam("houseId")Long houseId) {
        return sectionService.getSectionForSelect(selectSearchRequest, houseId);
    }
    @GetMapping("/get-apartments")
    public @ResponseBody Page<ApartmentNumberResponse> getApartments(SelectSearchRequest selectSearchRequest,
                                                                     @RequestParam("houseId")Long houseId,
                                                                     @RequestParam("sectionId")Long sectionId) {
        return apartmentService.getApartmentsForSelect(selectSearchRequest, houseId, sectionId);
    }

    @GetMapping("/get-services")
    public @ResponseBody Page<ServiceNameResponse> getServices(SelectSearchRequest selectSearchRequest) {
        return servicesService.getServicesForSelect(selectSearchRequest);
    }
    @PostMapping("/add")
    public ResponseEntity<?> createMeterReading(@ModelAttribute @Valid MeterReadingRequest meterReadingRequest,
                                                HttpServletRequest request) {
        meterReadingService.createMeterReading(meterReadingRequest);
        String url = request.getRequestURL().toString();
        int index = url.lastIndexOf("/");
        String returnUrl = url.substring(0, index);
        return new ResponseEntity<>(returnUrl,HttpStatus.OK);
    }
    @GetMapping("/edit/{id}")
    public ModelAndView getMeterReadingPageForEdit() {
        ModelAndView modelAndView = new ModelAndView("meter-readings/meter-reading");
        modelAndView.addObject("statusLink", "../get-statuses");
        modelAndView.addObject("houseLink", "../get-houses");
        modelAndView.addObject("sectionLink", "../get-sections");
        modelAndView.addObject("apartmentLink", "../get-apartments");
        modelAndView.addObject("serviceLink", "../get-services");
        return modelAndView;
    }
    @GetMapping("/get-reading/{id}")
    public @ResponseBody MeterReadingResponse getReading(@PathVariable Long id) {
        return meterReadingService.getMeterReadingResponse(id);
    }
    @PostMapping("/edit/{id}")
    public ResponseEntity<?> updateMeterReading(@PathVariable Long id,
                                                MeterReadingRequest meterReadingRequest,
                                                HttpServletRequest request) {
        meterReadingService.updateMeterReading(id, meterReadingRequest);
        String url = request.getRequestURL().toString();
        int index = url.lastIndexOf("/");
        url = url.substring(0, index - 5);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }
    @GetMapping("/get-number")
    public @ResponseBody String getNumber() {
        return meterReadingService.createNumber();
    }

}
