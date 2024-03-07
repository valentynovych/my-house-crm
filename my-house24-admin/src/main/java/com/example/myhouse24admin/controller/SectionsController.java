package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.houses.SectionResponse;
import com.example.myhouse24admin.service.SectionService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/sections")
public class SectionsController {

    private final SectionService sectionService;

    public SectionsController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping("get-sections-by-house/{houseId}")
    public @ResponseBody ResponseEntity<?> getSectionsByHouseId(@PathVariable Long houseId,
                                                                @RequestParam int page,
                                                                @RequestParam int pageSize,
                                                                @RequestParam String name) {
        Page<SectionResponse> sectionResponses = sectionService.getSectionsByHouseId(houseId, page, pageSize, name);
        return new ResponseEntity<>(sectionResponses, HttpStatus.OK);
    }
}
