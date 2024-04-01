package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.OwnerPasswordResetToken;
import com.example.myhouse24user.model.authentication.EmailRequest;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import com.example.myhouse24user.repository.OwnerPasswordResetTokenRepo;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerPasswordResetTokenServiceImplTest {
    @Mock
    private OwnerPasswordResetTokenRepo ownerPasswordResetTokenRepo;
    @Mock
    private ApartmentOwnerRepo apartmentOwnerRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private OwnerPasswordResetTokenServiceImpl ownerPasswordResetTokenService;
    private static OwnerPasswordResetToken ownerPasswordResetToken;
    @BeforeAll
    public static void setUp() {
        ownerPasswordResetToken = new OwnerPasswordResetToken();
        ownerPasswordResetToken.setUsed(false);
        ownerPasswordResetToken.setExpirationDate();
        ownerPasswordResetToken.setApartmentOwner(new ApartmentOwner());
    }
    @Test
    void createOrUpdatePasswordResetToken_Should_Create_PasswordResetToken() {
        when(apartmentOwnerRepo.findByEmail(anyString())).thenReturn(Optional.of(new ApartmentOwner()));
        when(ownerPasswordResetTokenRepo.save(any(OwnerPasswordResetToken.class))).thenReturn(new OwnerPasswordResetToken());

        String token = ownerPasswordResetTokenService.createOrUpdatePasswordResetToken(new EmailRequest("email"));
        assertThat(token).isNotEmpty();

        verify(apartmentOwnerRepo, times(1)).findByEmail(anyString());
        verify(ownerPasswordResetTokenRepo, times(1)).save(any(OwnerPasswordResetToken.class));

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(ownerPasswordResetTokenRepo);
    }
    @Test
    void createOrUpdatePasswordResetToken_Should_Update_PasswordResetToken() {
        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setOwnerPasswordResetToken(new OwnerPasswordResetToken());
        when(apartmentOwnerRepo.findByEmail(anyString())).thenReturn(Optional.of(apartmentOwner));
        when(apartmentOwnerRepo.save(any(ApartmentOwner.class))).thenReturn(new ApartmentOwner());

        String token = ownerPasswordResetTokenService.createOrUpdatePasswordResetToken(new EmailRequest("email"));
        assertThat(token).isNotEmpty();

        verify(apartmentOwnerRepo, times(1)).findByEmail(anyString());
        verify(apartmentOwnerRepo, times(1)).save(any(ApartmentOwner.class));

        verifyNoMoreInteractions(apartmentOwnerRepo);
    }

    @Test
    void isPasswordResetTokenValid_Should_Not_Be_Valid() {
        when(ownerPasswordResetTokenRepo.findByToken(anyString())).thenReturn(Optional.empty());

        boolean isValid = ownerPasswordResetTokenService.isPasswordResetTokenValid("token");
        assertThat(isValid).isFalse();

        verify(ownerPasswordResetTokenRepo, times(1)).findByToken(anyString());
        verifyNoMoreInteractions(ownerPasswordResetTokenRepo);
    }
    @Test
    void isPasswordResetTokenValid_Should_Be_Valid() {
        when(ownerPasswordResetTokenRepo.findByToken(anyString())).thenReturn(Optional.of(ownerPasswordResetToken));

        boolean isValid = ownerPasswordResetTokenService.isPasswordResetTokenValid("token");
        assertThat(isValid).isTrue();

        verify(ownerPasswordResetTokenRepo, times(1)).findByToken(anyString());
        verifyNoMoreInteractions(ownerPasswordResetTokenRepo);
    }
    @Test
    void updatePassword_Should_Update_Password() {
        when(ownerPasswordResetTokenRepo.findByToken(anyString())).thenReturn(Optional.of(ownerPasswordResetToken));
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(ownerPasswordResetTokenRepo.save(any(OwnerPasswordResetToken.class))).thenReturn(new OwnerPasswordResetToken());

        ownerPasswordResetTokenService.updatePassword("token", "password");

        verify(ownerPasswordResetTokenRepo, times(1)).findByToken(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());

        verify(ownerPasswordResetTokenRepo, times(1)).save(any(OwnerPasswordResetToken.class));

        verifyNoMoreInteractions(ownerPasswordResetTokenRepo);
    }
    @Test
    void updatePassword_Should_Throw_EntityNotFoundException() {
        when(ownerPasswordResetTokenRepo.findByToken(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                ownerPasswordResetTokenService.updatePassword("token", "password"));

        verify(ownerPasswordResetTokenRepo, times(1)).findByToken(anyString());

        verifyNoMoreInteractions(ownerPasswordResetTokenRepo);
    }
}