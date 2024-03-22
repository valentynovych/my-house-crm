package com.example.myhouse24rest.controller;

import com.example.myhouse24rest.model.profile.ProfileResponse;
import com.example.myhouse24rest.service.ProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/profile")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("get-profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        ProfileResponse profile = profileService.getProfile(principal);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }
}
