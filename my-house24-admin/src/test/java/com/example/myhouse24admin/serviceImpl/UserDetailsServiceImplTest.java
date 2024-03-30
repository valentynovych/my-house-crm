package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.entity.StaffStatus;
import com.example.myhouse24admin.repository.StaffRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.example.myhouse24admin.config.TestConfig.STAFF_EMAIL;
import static com.example.myhouse24admin.config.TestConfig.STAFF_PASSWORD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private StaffRepo staffRepo;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_WhenStaffFoundAndIsNotDisabled() {
        // given
        Staff staff = new Staff();
        staff.setEmail(STAFF_EMAIL);
        staff.setStatus(StaffStatus.ACTIVE);
        staff.setPassword(STAFF_PASSWORD);

        // when
        when(staffRepo.findByEmail(eq(STAFF_EMAIL)))
                .thenReturn(Optional.of(staff));

        UserDetails userDetails = userDetailsService.loadUserByUsername(STAFF_EMAIL);

        // then
        assertEquals(STAFF_EMAIL, userDetails.getUsername());
        assertEquals(STAFF_PASSWORD, userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_WhenStaffFoundAndIsDisabled() {
        // given
        Staff staff = new Staff();
        staff.setEmail(STAFF_EMAIL);
        staff.setStatus(StaffStatus.DISABLED);
        staff.setPassword(STAFF_PASSWORD);

        // when
        when(staffRepo.findByEmail(eq(STAFF_EMAIL)))
                .thenReturn(Optional.of(staff));

        UserDetails userDetails = userDetailsService.loadUserByUsername(STAFF_EMAIL);

        // then
        assertEquals(STAFF_EMAIL, userDetails.getUsername());
        assertEquals(STAFF_PASSWORD, userDetails.getPassword());
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_WhenStaffNotFound() {
        // when
        when(staffRepo.findByEmail(eq(STAFF_EMAIL)))
                .thenReturn(Optional.empty());

        // then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(STAFF_EMAIL));
    }
}