package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.houses.FloorResponse;
import com.example.myhouse24admin.service.FloorService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/floors")
public class FloorsController {

    private final FloorService floorService;

    public FloorsController(FloorService floorService) {
        this.floorService = floorService;
    }

    @GetMapping("get-floors-by-house/{houseId}")
    public @ResponseBody ResponseEntity<?> getFloorsByHouseId(@PathVariable Long houseId,
                                                                @RequestParam int page,
                                                                @RequestParam int pageSize,
                                                                @RequestParam String name) {
        Page<FloorResponse> floorResponses = floorService.getFloorsByHouseId(houseId, page, pageSize, name);
        return new ResponseEntity<>(floorResponses, HttpStatus.OK);
    }
}
