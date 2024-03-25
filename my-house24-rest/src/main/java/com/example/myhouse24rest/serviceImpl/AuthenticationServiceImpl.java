package com.example.myhouse24rest.serviceImpl;

import com.example.myhouse24rest.entity.ApartmentOwner;
import com.example.myhouse24rest.entity.OwnerRefreshToken;
import com.example.myhouse24rest.model.auth.ApartmentOwnerDetails;
import com.example.myhouse24rest.model.auth.AuthRequest;
import com.example.myhouse24rest.model.auth.JwtResponse;
import com.example.myhouse24rest.model.auth.RefreshTokenRequest;
import com.example.myhouse24rest.repository.OwnerRefreshTokenRepository;
import com.example.myhouse24rest.service.AuthenticationService;
import com.example.myhouse24rest.utils.JwtTokenUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final OwnerRefreshTokenRepository ownerRefreshTokenRepository;
    private final Logger logger = LogManager.getLogger(AuthenticationServiceImpl.class);

    public AuthenticationServiceImpl(JwtTokenUtils jwtTokenUtils, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService, OwnerRefreshTokenRepository ownerRefreshTokenRepository) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.ownerRefreshTokenRepository = ownerRefreshTokenRepository;
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
                createRefreshToken(userDetails));
        logger.info("login() - Login success, response: {}", jwtResponse);
        return jwtResponse;
    }

    @Override
    public JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        logger.info("refreshToken() - Refresh token request: {}", refreshTokenRequest);

        if (!checkValidRefreshToken(refreshTokenRequest)) {
            logger.warn("refreshToken() - Refresh token expired");
            throw new BadCredentialsException("Refresh token expired");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshTokenRequest.getEmail());
        JwtResponse jwtResponse = new JwtResponse(
                jwtTokenUtils.createAccessToken(userDetails),
                createRefreshToken(userDetails));
        logger.info("refreshToken() - Refresh token success, response: {}", jwtResponse);
        return jwtResponse;

    }

    private boolean checkValidRefreshToken(RefreshTokenRequest refreshToken) {
        Optional<OwnerRefreshToken> ownerRefreshToken =
                ownerRefreshTokenRepository.findByRefreshTokenAndOwner_Email(refreshToken.getRefreshToken(), refreshToken.getEmail());
        if (ownerRefreshToken.isPresent() && ownerRefreshToken.get().getExpiredDate().isAfter(Instant.now())) {
            logger.info("checkRefreshToken() - Refresh token valid");
            return true;
        }
        return false;
    }

    private String createRefreshToken(UserDetails userDetails) {
        logger.info("createRefreshToken() - User details: {}", userDetails);
        String refreshToken = jwtTokenUtils.createRefreshToken(userDetails);
        ApartmentOwnerDetails owner = (ApartmentOwnerDetails) userDetails;
        saveRefreshToken(owner.getOwner(), refreshToken);
        logger.info("createRefreshToken() - Refresh token created: {}", refreshToken);
        return refreshToken;
    }

    private void saveRefreshToken(ApartmentOwner owner, String refreshToken) {
        logger.info("saveRefreshToken() - Refresh token: {}", refreshToken);
        OwnerRefreshToken ownerRefreshToken = ownerRefreshTokenRepository
                .findByOwner_Email(owner.getEmail()).orElse(new OwnerRefreshToken());
        ownerRefreshToken.setOwner(owner);
        ownerRefreshToken.setRefreshToken(refreshToken);
        ownerRefreshToken.setExpiredDate(jwtTokenUtils.getExpiredDate(refreshToken));
        ownerRefreshTokenRepository.save(ownerRefreshToken);
        logger.info("saveRefreshToken() - Refresh token saved: {}", ownerRefreshToken);
    }
}
