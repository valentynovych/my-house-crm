package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.OwnerPasswordResetToken;
import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.model.authentication.EmailRequest;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import com.example.myhouse24user.repository.OwnerPasswordResetTokenRepo;
import com.example.myhouse24user.service.OwnerPasswordResetTokenService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class OwnerPasswordResetTokenServiceImpl implements OwnerPasswordResetTokenService {
    private final OwnerPasswordResetTokenRepo ownerPasswordResetTokenRepo;
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LogManager.getLogger(OwnerPasswordResetTokenServiceImpl.class);

    public OwnerPasswordResetTokenServiceImpl(OwnerPasswordResetTokenRepo ownerPasswordResetTokenRepo,
                                              ApartmentOwnerRepo apartmentOwnerRepo,
                                              PasswordEncoder passwordEncoder) {
        this.ownerPasswordResetTokenRepo = ownerPasswordResetTokenRepo;
        this.apartmentOwnerRepo = apartmentOwnerRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String createOrUpdatePasswordResetToken(EmailRequest emailRequest) {
        logger.info("createOrUpdatePasswordResetToken() - Creating or updating password reset token by email "+emailRequest.email());
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findByEmail(emailRequest.email()).orElseThrow(()-> new EntityNotFoundException("Owner was not found by email "+emailRequest.email()));
        String token;
        if(ownerHasToken(apartmentOwner)){
           token = updatePasswordResetToken(apartmentOwner);
            logger.info("createOrUpdatePasswordResetToken() - Password reset token was updated");
        } else {
            token = createPasswordResetToken(apartmentOwner);
            logger.info("createOrUpdatePasswordResetToken() - Password reset token was created");

        }
        return token;
    }
    private boolean ownerHasToken(ApartmentOwner apartmentOwner){
        return apartmentOwner.getOwnerPasswordResetToken() != null;
    }
    private String updatePasswordResetToken(ApartmentOwner apartmentOwner) {
        String token = UUID.randomUUID().toString();
        apartmentOwner.getOwnerPasswordResetToken().setToken(token);
        apartmentOwner.getOwnerPasswordResetToken().setExpirationDate();
        apartmentOwnerRepo.save(apartmentOwner);
        return token;
    }
    private String createPasswordResetToken(ApartmentOwner apartmentOwner) {
        String token = UUID.randomUUID().toString();
        OwnerPasswordResetToken passwordResetToken = new OwnerPasswordResetToken(token,apartmentOwner);
        ownerPasswordResetTokenRepo.save(passwordResetToken);
        return token;
    }

    @Override
    public boolean isPasswordResetTokenValid(String token) {
        logger.info("isPasswordResetTokenValid() - Checking if password reset token "+token+" valid");
        Optional<OwnerPasswordResetToken> passwordResetToken = ownerPasswordResetTokenRepo.findByToken(token);
        boolean isValid = passwordResetToken.isPresent() && !passwordResetToken.get().getExpirationDate().isBefore(Instant.now());
        logger.info("isPasswordResetTokenValid() - Password reset token was checked");
        return isValid;
    }

    @Override
    public void updatePassword(String token, String password) {
        logger.info("updatePassword() - Updating password by password reset token "+token);
        OwnerPasswordResetToken passwordResetToken = ownerPasswordResetTokenRepo.findByToken(token).orElseThrow(()-> new EntityNotFoundException("Password reset token was not found by token "+token));
        ApartmentOwner apartmentOwner = passwordResetToken.getApartmentOwner();
        apartmentOwner.setPassword(passwordEncoder.encode(password));
        apartmentOwner.setStatus(OwnerStatus.ACTIVE);
        ownerPasswordResetTokenRepo.save(passwordResetToken);
        logger.info("updatePassword() - Password was updated");
    }
}
