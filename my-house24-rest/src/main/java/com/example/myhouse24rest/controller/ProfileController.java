package com.example.myhouse24rest.controller;

import com.example.myhouse24rest.model.error.CustomErrorResponse;
import com.example.myhouse24rest.model.profile.ProfileResponse;
import com.example.myhouse24rest.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/profile")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profile", description = "Profile API")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(
            summary = "Get profile",
            description = "Get apartment owner profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponse.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))})
    })
    @GetMapping("get-profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        ProfileResponse profile = profileService.getProfile(principal);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }
}
