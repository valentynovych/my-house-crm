package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.owner.ViewOwnerResponse;
import com.example.myhouse24user.service.ApartmentOwnerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
}
