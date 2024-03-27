package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.PasswordResetToken;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.authentication.EmailRequest;
import com.example.myhouse24admin.repository.PasswordResetTokenRepo;
import com.example.myhouse24admin.repository.StaffRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceImplTest {
    @Mock
    private PasswordResetTokenRepo passwordResetTokenRepo;
    @Mock
    private StaffRepo staffRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private PasswordResetTokenServiceImpl passwordResetTokenService;
    private static PasswordResetToken passwordResetToken;
    @BeforeAll
    public static void setUp() {
        passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUsed(false);
        passwordResetToken.setExpirationDate();
        passwordResetToken.setStaff(new Staff());
    }

    @Test
    void createOrUpdatePasswordResetToken_Should_Create_PasswordResetToken() {
        when(staffRepo.findByEmail(anyString())).thenReturn(Optional.of(new Staff()));
        when(passwordResetTokenRepo.save(any(PasswordResetToken.class)))
                .thenReturn(new PasswordResetToken());

        String token = passwordResetTokenService.createOrUpdatePasswordResetToken(new EmailRequest("email"));
        assertThat(token).isNotEmpty();

        verify(staffRepo, times(1)).findByEmail(anyString());
        verify(passwordResetTokenRepo, times(1))
                .save(any(PasswordResetToken.class));

        verifyNoMoreInteractions(staffRepo);
        verifyNoMoreInteractions(passwordResetTokenRepo);

    }
    @Test
    void createOrUpdatePasswordResetToken_Should_Update_PasswordResetToken() {
        Staff staff = new Staff();
        staff.setPasswordResetToken(new PasswordResetToken());
        when(staffRepo.findByEmail(anyString())).thenReturn(Optional.of(staff));
        when(staffRepo.save(any(Staff.class)))
                .thenReturn(new Staff());

        String token = passwordResetTokenService.createOrUpdatePasswordResetToken(new EmailRequest("email"));
        assertThat(token).isNotEmpty();

        verify(staffRepo, times(1)).findByEmail(anyString());
        verify(staffRepo, times(1)).save(any(Staff.class));

        verifyNoMoreInteractions(staffRepo);
        verifyNoMoreInteractions(passwordResetTokenRepo);

    }

    @Test
    void createOrUpdatePasswordResetToken_Should_Throw_EntityNotFoundException() {
        when(staffRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> passwordResetTokenService
                .createOrUpdatePasswordResetToken(new EmailRequest("email")));

        verify(staffRepo, times(1)).findByEmail(anyString());

        verifyNoMoreInteractions(staffRepo);
    }
    @Test
    void isPasswordResetTokenValid() {
        when(passwordResetTokenRepo.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));

        boolean isValid = passwordResetTokenService.isPasswordResetTokenValid("token");
        assertThat(isValid).isTrue();

        verify(passwordResetTokenRepo, times(1)).findByToken(anyString());

        verifyNoMoreInteractions(passwordResetTokenRepo);
    }

    @Test
    void updatePassword() {
        when(passwordResetTokenRepo.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(passwordResetTokenRepo.save(any(PasswordResetToken.class)))
                .thenReturn(new PasswordResetToken());

        passwordResetTokenService.updatePassword("token","password");

        verify(passwordResetTokenRepo, times(1)).findByToken(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(passwordResetTokenRepo, times(1))
                .save(any(PasswordResetToken.class));

        verifyNoMoreInteractions(passwordResetTokenRepo);
        verifyNoMoreInteractions(passwordEncoder);

    }
}