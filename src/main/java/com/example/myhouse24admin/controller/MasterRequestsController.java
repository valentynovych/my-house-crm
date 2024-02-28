package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24admin.service.MasterRequestService;
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
@RequestMapping("/admin/master-requests")
public class MasterRequestsController {

    private final MasterRequestService masterRequestService;

    public MasterRequestsController(MasterRequestService masterRequestService) {
        this.masterRequestService = masterRequestService;
    }

    @GetMapping("add-request")
    public ModelAndView viewAddMasterRequest() {
        return new ModelAndView("master-requests/add-master-request");
    }


    @PostMapping("add-request")
    public ResponseEntity<?> addNewRequest(@ModelAttribute @Valid MasterRequestAddRequest request) {
        masterRequestService.addNewMasterRequest(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
