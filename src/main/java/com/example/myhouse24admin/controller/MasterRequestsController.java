package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestEditRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestResponse;
import com.example.myhouse24admin.model.masterRequest.MasterRequestTableResponse;
import com.example.myhouse24admin.service.MasterRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

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

    @GetMapping("")
    public ModelAndView viewMasterRequests() {
        return new ModelAndView("master-requests/master-requests");
    }

    @GetMapping("edit-request/{masterRequestId}")
    public ModelAndView viewEditMasterRequest(@PathVariable Long masterRequestId) {
        return new ModelAndView("master-requests/edit-master-request");
    }

    @PostMapping("add-request")
    public ResponseEntity<?> addNewRequest(@ModelAttribute @Valid MasterRequestAddRequest request) {
        masterRequestService.addNewMasterRequest(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-requests")
    public @ResponseBody ResponseEntity<?> getMasterRequests(@RequestParam int page,
                                                             @RequestParam int pageSize,
                                                             @RequestParam Map<String, String> searchParams) {
        Page<MasterRequestTableResponse> tableResponse = masterRequestService.getMasterRequests(page, pageSize, searchParams);
        return new ResponseEntity<>(tableResponse, HttpStatus.OK);
    }

    @GetMapping("get-request/{masterRequestId}")
    public @ResponseBody ResponseEntity<?> getMasterRequests(@PathVariable Long masterRequestId) {
        MasterRequestResponse response = masterRequestService.getMasterRequestById(masterRequestId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("edit-request/{masterRequestId}")
    public ResponseEntity<?> updateRequest(@PathVariable Long masterRequestId,
                                           @ModelAttribute @Valid MasterRequestEditRequest request) {
        masterRequestService.updateMasterRequest(masterRequestId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("delete/{masterRequestId}")
    public ResponseEntity<?> deleteMasterRequest(@PathVariable Long masterRequestId) {
        boolean isDeleted = masterRequestService.deleteMasterRequestById(masterRequestId);

        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.LOCKED);
    }

}
