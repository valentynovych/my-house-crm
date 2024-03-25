package com.example.myhouse24rest.serviceImpl;

import com.example.myhouse24rest.entity.ApartmentOwner;
import com.example.myhouse24rest.entity.OwnerRefreshToken;
import com.example.myhouse24rest.model.auth.ApartmentOwnerDetails;
import com.example.myhouse24rest.model.auth.AuthRequest;
import com.example.myhouse24rest.model.auth.JwtResponse;
import com.example.myhouse24rest.model.auth.RefreshTokenRequest;
import com.example.myhouse24rest.repository.OwnerRefreshTokenRepository;
import com.example.myhouse24rest.utils.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    private AuthenticationServiceImpl authenticationService;
    private JwtTokenUtils jwtTokenUtils;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private OwnerRefreshTokenRepository ownerRefreshTokenRepository;
    private final String email = "test@example.com";
    private final String password = "password";
    private String accessToken;
    private String refreshToken;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        jwtTokenUtils = new JwtTokenUtils();
        authenticationService = new AuthenticationServiceImpl(jwtTokenUtils, passwordEncoder, userDetailsService, ownerRefreshTokenRepository);

        ReflectionTestUtils.setField(jwtTokenUtils, "accessTokenLifetime", Duration.ofHours(24));
        ReflectionTestUtils.setField(jwtTokenUtils, "refreshTokenLifetime", Duration.ofDays(7));
        ReflectionTestUtils.setField(jwtTokenUtils, "tokenSecret", "secretForJWTWithoutHS256Encoding");

        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setEmail(email);
        apartmentOwner.setPassword(password);
        userDetails = new ApartmentOwnerDetails(apartmentOwner);

        refreshToken = jwtTokenUtils.createRefreshToken(userDetails);
        accessToken = jwtTokenUtils.createAccessToken(userDetails);
    }

    @Test
    public void testLogin() {
        AuthRequest loginRequest = new AuthRequest("test@example.com", "password");
        when(userDetailsService.loadUserByUsername(loginRequest.email())).thenReturn(userDetails);
        when(passwordEncoder.matches(loginRequest.password(), userDetails.getPassword())).thenReturn(true);

        JwtResponse result = authenticationService.login(loginRequest);

        assertTrue(jwtTokenUtils.validateJwtToken(result.accessToken()));
        assertTrue(jwtTokenUtils.validateJwtToken(result.refreshToken()));
        verify(userDetailsService, times(1)).loadUserByUsername(loginRequest.email());
        verify(passwordEncoder, times(1)).matches(loginRequest.password(), userDetails.getPassword());
    }

    @Test
    public void testLogin_BadCredentials() {
        AuthRequest loginRequest = new AuthRequest("test@example.com", "password");
        when(userDetailsService.loadUserByUsername(loginRequest.email())).thenReturn(userDetails);
        when(passwordEncoder.matches(loginRequest.password(), userDetails.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(loginRequest));
    }

    @Test
    public void testRefreshToken() {

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setEmail("test@example.com");
        refreshTokenRequest.setRefreshToken(refreshToken);

        ApartmentOwner owner = new ApartmentOwner();
        owner.setEmail(refreshTokenRequest.getEmail());
        owner.setEmail("test@example.com");

        OwnerRefreshToken ownerRefreshToken = new OwnerRefreshToken();
        ownerRefreshToken.setRefreshToken(refreshToken);
        ownerRefreshToken.setOwner(owner);
        ownerRefreshToken.setExpiredDate(Instant.now().plus(Duration.ofDays(7)));

        when(userDetailsService.loadUserByUsername(refreshTokenRequest.getEmail())).thenReturn(userDetails);
        when(ownerRefreshTokenRepository.findByRefreshTokenAndOwner_Email(refreshTokenRequest.getRefreshToken(),
                refreshTokenRequest.getEmail())).thenReturn(Optional.of(ownerRefreshToken));

        JwtResponse result = authenticationService.refreshToken(refreshTokenRequest);

        String resultAccessToken = result.accessToken();
        String resultRefreshedToken = result.refreshToken();

        assertTrue(jwtTokenUtils.validateJwtToken(resultAccessToken));
        assertTrue(jwtTokenUtils.validateJwtToken(resultRefreshedToken));

        Instant expiredDateAccessToken = jwtTokenUtils.getExpiredDate(resultAccessToken);
        Instant expiredDateRefreshToken = jwtTokenUtils.getExpiredDate(resultRefreshedToken);

        assertTrue(expiredDateAccessToken.isAfter(Instant.now().plus(Duration.ofHours(23))));
        assertTrue(expiredDateRefreshToken.isAfter(Instant.now().plus(Duration.ofDays(6))));

        jwtTokenUtils.getExpiredDate(resultRefreshedToken);

        verify(userDetailsService, times(1)).loadUserByUsername(refreshTokenRequest.getEmail());
    }

    @Test
    public void testRefreshToken_ExpiredRefreshToken() {
        ReflectionTestUtils.setField(jwtTokenUtils, "refreshTokenLifetime", Duration.ofHours(1).dividedBy(2));
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setEmail("test@example.com");
        refreshTokenRequest.setRefreshToken(jwtTokenUtils.createRefreshToken(userDetails));

        assertThrows(BadCredentialsException.class, () -> authenticationService.refreshToken(refreshTokenRequest));
    }
}