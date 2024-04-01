package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock
    private ApartmentOwnerRepo apartmentOwnerRepo;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_Should_Return_ApartmentOwnerDetails() {
        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setStatus(OwnerStatus.DISABLED);

        when(apartmentOwnerRepo.findByEmail(anyString())).thenReturn(Optional.of(apartmentOwner));

        userDetailsService.loadUserByUsername("email");

        verify(apartmentOwnerRepo, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(apartmentOwnerRepo);
    }
    @Test
    void loadUserByUsername_Should_Throw_UsernameNotFoundException() {
        when(apartmentOwnerRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("email"));

        verify(apartmentOwnerRepo, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(apartmentOwnerRepo);
    }
}