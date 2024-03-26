package com.example.myhouse24rest.config;

import com.example.myhouse24rest.configuration.JwtRequestFilter;
import com.example.myhouse24rest.entity.ApartmentOwner;
import com.example.myhouse24rest.model.auth.ApartmentOwnerDetails;
import com.example.myhouse24rest.service.ApartmentService;
import com.example.myhouse24rest.service.AuthenticationService;
import com.example.myhouse24rest.service.MessageService;
import com.example.myhouse24rest.service.ProfileService;
import com.example.myhouse24rest.serviceImpl.UserDetailsServiceImpl;
import com.example.myhouse24rest.utils.JwtTokenUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfiguration {

    @Bean
    public JwtTokenUtils jwtTokenUtils() {
        JwtTokenUtils jwtTokenUtils = new JwtTokenUtils();
        ReflectionTestUtils.setField(jwtTokenUtils, "accessTokenLifetime", Duration.ofHours(24));
        ReflectionTestUtils.setField(jwtTokenUtils, "refreshTokenLifetime", Duration.ofDays(7));
        ReflectionTestUtils.setField(jwtTokenUtils, "tokenSecret", "secretForJWTWithoutHS256Encoding");
        return jwtTokenUtils;
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(JwtTokenUtils jwtTokenUtils) {
        return new JwtRequestFilter(jwtTokenUtils, mock(UserDetailsServiceImpl.class));
    }

    @Bean
    public UserDetails userDetails() {
        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setEmail("test.email@example.com");
        apartmentOwner.setPassword("Password123!@");
        return new ApartmentOwnerDetails(apartmentOwner);
    }

    @Bean
    public ApartmentService apartmentService() {
        return mock(ApartmentService.class);
    }

    @Bean
    public ProfileService profileService() {
        return mock(ProfileService.class);
    }

    @Bean
    public AuthenticationService authenticationService() {
        return mock(AuthenticationService.class);
    }

    @Bean
    public MessageService messageService() {
        return mock(MessageService.class);
    }
}
