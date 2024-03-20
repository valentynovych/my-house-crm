package com.example.myhouse24user.controller;

import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.model.owner.ApartmentOwnerRequest;
import com.example.myhouse24user.model.owner.EditOwnerResponse;
import com.example.myhouse24user.model.owner.ViewOwnerResponse;
import com.example.myhouse24user.service.ApartmentOwnerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/cabinet/profile")
public class ProfileController {
    private final ApartmentOwnerService apartmentOwnerService;

    public ProfileController(ApartmentOwnerService apartmentOwnerService) {
        this.apartmentOwnerService = apartmentOwnerService;
    }
    @GetMapping("")
    public ModelAndView getViewProfilePage() {
        return new ModelAndView("profile/view-profile");
    }
    @GetMapping("/get")
    public @ResponseBody ViewOwnerResponse getProfile() {
        return apartmentOwnerService.getViewOwnerResponse();
    }
    @GetMapping("/edit")
    public ModelAndView getEditProfilePage() {
        return new ModelAndView("profile/edit-profile");
    }
    @GetMapping("/edit/get")
    public @ResponseBody EditOwnerResponse getProfileForEdit() {
        return apartmentOwnerService.getEditOwnerResponse();
    }
    @PostMapping("/edit")
    public @ResponseBody ResponseEntity<?> updateProfile(@Valid @ModelAttribute ApartmentOwnerRequest apartmentOwnerRequest,
                                                         @RequestParam(name = "avatar", required = false)
                                                         MultipartFile avatar,
                                                         HttpServletRequest request) {
        apartmentOwnerService.updateProfile(apartmentOwnerRequest, avatar);
        String url = request.getRequestURL().toString();
        int index = url.lastIndexOf("/");
        String returnUrl = url.substring(0, index);
        return new ResponseEntity<>(returnUrl, HttpStatus.OK);
    }
    @GetMapping("/get-statuses")
    public @ResponseBody OwnerStatus[] getOwnerStatuses() {
        return OwnerStatus.values();
    }
}
