package com.example.myhouse24rest.serviceImpl;

import com.example.myhouse24rest.entity.ApartmentOwner;
import com.example.myhouse24rest.mapper.ApartmentOwnerMapper;
import com.example.myhouse24rest.model.profile.ProfileResponse;
import com.example.myhouse24rest.repository.ApartmentOwnerRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private ApartmentOwnerRepo apartmentOwnerRepo;
    @Mock
    private ApartmentOwnerMapper apartmentOwnerMapper;
    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    public void testGetProfile_WithValidPrincipal() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setEmail("test@example.com");
        apartmentOwner.setFirstName("John");
        apartmentOwner.setLastName("Doe");
        apartmentOwner.setMiddleName("");
        Optional<ApartmentOwner> optionalApartmentOwner = Optional.of(apartmentOwner);
        when(apartmentOwnerRepo.findByEmail("test@example.com")).thenReturn(optionalApartmentOwner);

        ProfileResponse profileResponse = new ProfileResponse(
                "John Doe",
                "00001",
                "test@example.com",
                "+380500000000",
                new ArrayList<>());

        when(apartmentOwnerMapper.apartmentOwnerToProfileResponse(apartmentOwner)).thenReturn(profileResponse);
        ProfileResponse result = profileService.getProfile(principal);

        assertEquals("John Doe", result.fullName());
        assertEquals("test@example.com", result.email());
        verify(apartmentOwnerRepo, times(1)).findByEmail("test@example.com");
        verify(apartmentOwnerMapper, times(1)).apartmentOwnerToProfileResponse(apartmentOwner);
    }

    @Test
    public void testGetProfile_WithInvalidPrincipal() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        when(apartmentOwnerRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> profileService.getProfile(principal));

        verify(apartmentOwnerRepo, times(1)).findByEmail("test@example.com");
    }

}