package com.example.myhouse24rest.controller;

import com.example.myhouse24rest.service.AuthenticationService;
import com.example.myhouse24rest.model.auth.AuthRequest;
import com.example.myhouse24rest.model.auth.JwtResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(implementation = JwtResponse.class))}
            ),})
    @PostMapping("signin")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authenticationService.login(loginRequest);
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
