package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.entity.Role;
import com.example.myhouse24admin.model.staff.StaffEditRequest;
import com.example.myhouse24admin.model.staff.StaffResponse;
import com.example.myhouse24admin.service.StaffService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("system-settings/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("add")
    public ModelAndView viewAddStaff() {
        return new ModelAndView("system/staff/add-staff");
    }

    @GetMapping("")
    public ModelAndView viewStaff() {
        return new ModelAndView("system/staff/staff");
    }

    @GetMapping("edit-staff/{staffId}")
    public ModelAndView viewEditStaff(@PathVariable Long staffId) {
        return new ModelAndView("system/staff/edit-staff");
    }

    @PostMapping("add")
    @ResponseBody
    public ResponseEntity<?> addNewStaff(@Valid @ModelAttribute StaffEditRequest staffEditRequest) {
        staffService.addNewStaff(staffEditRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-roles")
    @ResponseBody
    public ResponseEntity<?> getStaffRoles() {
        List<Role> roles = staffService.getRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("get-statuses")
    @ResponseBody
    public ResponseEntity<?> getStaffStatuses() {
        List<String> statuses = staffService.getStatuses();
        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }

    @GetMapping("get-staff")
    @ResponseBody
    public ResponseEntity<?> getStaff(@RequestParam @Min(0) int page,
                                      @RequestParam @Min(1) int pageSize,
                                      @RequestParam(required = false) Map<String, String> searchParams) {
        Page<StaffResponse> staffResponses = staffService.getStaff(page, pageSize, searchParams);
        return new ResponseEntity<>(staffResponses, HttpStatus.OK);
    }
}
