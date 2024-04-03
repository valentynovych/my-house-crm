package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.OwnerPasswordResetToken;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import com.example.myhouse24admin.repository.OwnerPasswordResetTokenRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerPasswordResetTokenServiceImplTest {
    @Mock
    private OwnerPasswordResetTokenRepo ownerPasswordResetTokenRepo;
    @Mock
    private ApartmentOwnerRepo apartmentOwnerRepo;
    @InjectMocks
    private OwnerPasswordResetTokenServiceImpl ownerPasswordResetTokenService;
    @Test
    void createOrUpdatePasswordResetToken_Should_Create_Token() {
        when(apartmentOwnerRepo.findById(1L)).thenReturn(Optional.of(new ApartmentOwner()));
        when(ownerPasswordResetTokenRepo.save(any(OwnerPasswordResetToken.class)))
                .thenReturn(new OwnerPasswordResetToken());

        String token = ownerPasswordResetTokenService.createOrUpdatePasswordResetToken(1L);
        assertThat(token).isNotEmpty();

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verify(ownerPasswordResetTokenRepo, times(1))
                .save(any(OwnerPasswordResetToken.class));

        verifyNoMoreInteractions(apartmentOwnerRepo);
        verifyNoMoreInteractions(ownerPasswordResetTokenRepo);
    }
    @Test
    void createOrUpdatePasswordResetToken_Should_Update_Token() {
        OwnerPasswordResetToken ownerPasswordResetToken = new OwnerPasswordResetToken();
        ApartmentOwner apartmentOwner = new ApartmentOwner();
        apartmentOwner.setOwnerPasswordResetToken(ownerPasswordResetToken);
        when(apartmentOwnerRepo.findById(1L)).thenReturn(Optional.of(apartmentOwner));
        when(apartmentOwnerRepo.save(any(ApartmentOwner.class)))
                .thenReturn(new ApartmentOwner());

        String token = ownerPasswordResetTokenService.createOrUpdatePasswordResetToken(1L);
        assertThat(token).isNotEmpty();

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());
        verify(apartmentOwnerRepo, times(1))
                .save(any(ApartmentOwner.class));

        verifyNoMoreInteractions(apartmentOwnerRepo);
    }

    @Test
    void createOrUpdatePasswordResetToken_Should_Throw_EntityNotFoundException() {
        when(apartmentOwnerRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> ownerPasswordResetTokenService
                .createOrUpdatePasswordResetToken(1L));

        verify(apartmentOwnerRepo, times(1)).findById(anyLong());

        verifyNoMoreInteractions(apartmentOwnerRepo);
    }
}