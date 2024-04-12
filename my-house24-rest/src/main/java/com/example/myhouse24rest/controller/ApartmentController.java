package com.example.myhouse24rest.controller;

import com.example.myhouse24rest.model.apartment.ApartmentShortResponse;
import com.example.myhouse24rest.model.apartment.ApartmentShortResponsePage;
import com.example.myhouse24rest.model.error.CustomErrorResponse;
import com.example.myhouse24rest.service.ApartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/v1/apartments")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Apartments", description = "Apartment API")
public class ApartmentController {


    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @Operation(
            summary = "Get all apartments",
            description = "Get all apartments from apartmentOwner, with pagination " +
                    "by page and pageSize")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApartmentShortResponsePage.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))}),
    })
    @GetMapping("get-all-apartments")
    public ResponseEntity<?> getAllApartments(@RequestParam(defaultValue = "0") @Min(0) int page,
                                              @RequestParam(defaultValue = "10") @Min(2) int pageSize,
                                              Principal principal) {
        Page<ApartmentShortResponse> allApartments = apartmentService.getAllApartments(page, pageSize, principal);
        return new ResponseEntity<>(allApartments, HttpStatus.OK);
    }
}
