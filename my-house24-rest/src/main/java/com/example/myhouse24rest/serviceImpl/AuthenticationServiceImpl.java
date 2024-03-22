package com.example.myhouse24rest.serviceImpl;

import com.example.myhouse24rest.service.AuthenticationService;
import com.example.myhouse24rest.model.auth.AuthRequest;
import com.example.myhouse24rest.model.auth.JwtResponse;
import com.example.myhouse24rest.utils.JwtTokenUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final Logger logger = LogManager.getLogger(AuthenticationServiceImpl.class);

    public AuthenticationServiceImpl(JwtTokenUtils jwtTokenUtils, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public JwtResponse login(AuthRequest loginRequest) {
        logger.info("login() - Login request: {}", loginRequest);
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());
        if (!passwordEncoder.matches(loginRequest.password(), userDetails.getPassword())) {
            logger.warn("login() - Bad credentials");
            throw new BadCredentialsException("Bad credentials");
        }

        JwtResponse jwtResponse = new JwtResponse(
                jwtTokenUtils.createAccessToken(userDetails),
                jwtTokenUtils.createRefreshToken(userDetails));
        logger.info("login() - Login success, response: {}", jwtResponse);
        return jwtResponse;
    }
}
