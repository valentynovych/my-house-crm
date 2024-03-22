package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.entity.OwnerPasswordResetToken;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import com.example.myhouse24admin.repository.OwnerPasswordResetTokenRepo;
import com.example.myhouse24admin.service.OwnerPasswordResetTokenService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class OwnerPasswordResetTokenServiceImpl implements OwnerPasswordResetTokenService {
    private final OwnerPasswordResetTokenRepo ownerPasswordResetTokenRepo;
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private final Logger logger = LogManager.getLogger(OwnerPasswordResetTokenServiceImpl.class);

    public OwnerPasswordResetTokenServiceImpl(OwnerPasswordResetTokenRepo ownerPasswordResetTokenRepo,
                                              ApartmentOwnerRepo apartmentOwnerRepo) {
        this.ownerPasswordResetTokenRepo = ownerPasswordResetTokenRepo;
        this.apartmentOwnerRepo = apartmentOwnerRepo;
    }

    @Override
    public String createOrUpdatePasswordResetToken(Long ownerId) {
        logger.info("createOrUpdatePasswordResetToken() - Creating or updating password reset token by owner id "+ownerId);
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findById(ownerId).orElseThrow(()-> new EntityNotFoundException("Owner was not found by id "+ownerId));
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
        apartmentOwner.getOwnerPasswordResetToken().setUsed(false);
        apartmentOwnerRepo.save(apartmentOwner);
        return token;
    }
    private String createPasswordResetToken(ApartmentOwner apartmentOwner) {
        String token = UUID.randomUUID().toString();
        OwnerPasswordResetToken passwordResetToken = new OwnerPasswordResetToken(token,apartmentOwner);
        ownerPasswordResetTokenRepo.save(passwordResetToken);
        return token;
    }
}
