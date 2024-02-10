package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.houses.HouseAddRequest;
import com.example.myhouse24admin.model.houses.HouseViewResponse;
import com.example.myhouse24admin.model.houses.HouseShortResponse;
import com.example.myhouse24admin.service.HouseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

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

    @GetMapping("")
    public ModelAndView viewHouses() {
        return new ModelAndView("houses/houses");
    }

    @GetMapping("view-house/{houseId}")
    public ModelAndView viewHouseCard(@PathVariable Long houseId) {
        return new ModelAndView("houses/view-house");
    }

    @PostMapping("add")
    public @ResponseBody ResponseEntity<?> addNewHouse(@ModelAttribute @Valid HouseAddRequest houseAddRequest) {
        houseService.addNewHouse(houseAddRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-houses")
    public @ResponseBody ResponseEntity<Page<HouseShortResponse>> getHouses(@RequestParam int page,
                                                                            @RequestParam int pageSize,
                                                                            @RequestParam(required = false)
                                                                            Map<String, String> searchParams) {
        Page<HouseShortResponse> houses = houseService.getHouses(page, pageSize, searchParams);
        return new ResponseEntity<>(houses, HttpStatus.OK);
    }

    @DeleteMapping("delete/{houseId}")
    public @ResponseBody ResponseEntity<?> deleteHouseById(@PathVariable @Min(1) Long houseId) {
        try {
            houseService.deleteHouseById(houseId);
        } catch (EntityNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-house/{houseId}")
    public @ResponseBody ResponseEntity<?> getHouseById(@PathVariable @Min(1) Long houseId) {
        HouseViewResponse houseViewResponse = houseService.getHouseById(houseId);
        return new ResponseEntity<>(houseViewResponse, HttpStatus.OK);
    }
}
