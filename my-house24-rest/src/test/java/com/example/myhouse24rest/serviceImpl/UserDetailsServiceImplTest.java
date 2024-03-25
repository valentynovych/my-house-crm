package com.example.myhouse24rest.serviceImpl;

import com.example.myhouse24rest.entity.ApartmentOwner;
import com.example.myhouse24rest.repository.ApartmentOwnerRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private ApartmentOwnerRepo apartmentOwnerRepo;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testLoadUserByUsername_WithValidUsername() {
        String username = "test@example.com";

        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setEmail(username);
        apartmentOwner.setPassword("test123");
        when(apartmentOwnerRepo.findByEmail(username)).thenReturn(Optional.of(apartmentOwner));

        UserDetails result = userDetailsService.loadUserByUsername(username);

        assertEquals("test@example.com", result.getUsername());
        assertEquals("test123", result.getPassword());
        verify(apartmentOwnerRepo, times(1)).findByEmail(username);
    }

    @Test
    public void testLoadUserByUsername_IsUsernameNotFound() {
        String username = "test@example.com";
        when(apartmentOwnerRepo.findByEmail(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
        verify(apartmentOwnerRepo, times(1)).findByEmail(username);
    }
}