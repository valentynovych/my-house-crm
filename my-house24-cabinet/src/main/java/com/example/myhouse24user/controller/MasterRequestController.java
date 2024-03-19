package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24user.model.masterRequest.MasterRequestTableResponse;
import com.example.myhouse24user.service.MasterRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@RequestMapping("cabinet/master-requests")
public class MasterRequestController {

    private final MasterRequestService masterRequestService;

    public MasterRequestController(MasterRequestService masterRequestService) {
        this.masterRequestService = masterRequestService;
    }

    @GetMapping("")
    public ModelAndView viewMasterRequests() {
        return new ModelAndView("master-requests/master-requests");
    }

    @GetMapping("add-request")
    public ModelAndView addMasterRequest() {
        return new ModelAndView("master-requests/add-master-request");
    }

    @GetMapping("get-requests")
    public ResponseEntity<?> getMasterRequests(@RequestParam("page") int page,
                                               @RequestParam("pageSize") int pageSize,
                                               Principal principal) {
        Page<MasterRequestTableResponse> masterRequests =
                masterRequestService.getMasterRequests(principal.getName(), page, pageSize);
        return new ResponseEntity<>(masterRequests, HttpStatus.OK);
    }

    @PostMapping("add-request")
    public ResponseEntity<?> addMasterRequest(@ModelAttribute @Valid MasterRequestAddRequest masterRequest,
                                              Principal principal) {
        masterRequestService.addMasterRequest(masterRequest, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteMasterRequest(@PathVariable("id") Long id) {
        masterRequestService.deleteMasterRequest(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
