package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.PasswordResetToken;
import com.example.myhouse24admin.entity.Staff;
import com.example.myhouse24admin.model.authentication.EmailRequest;
import com.example.myhouse24admin.repository.PasswordResetTokenRepo;
import com.example.myhouse24admin.repository.StaffRepo;
import com.example.myhouse24admin.service.PasswordResetTokenService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private final StaffRepo staffRepo;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LogManager.getLogger(PasswordResetTokenServiceImpl.class);

    public PasswordResetTokenServiceImpl(PasswordResetTokenRepo passwordResetTokenRepo, StaffRepo staffRepo, PasswordEncoder passwordEncoder) {
        this.passwordResetTokenRepo = passwordResetTokenRepo;
        this.staffRepo = staffRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String createOrUpdatePasswordResetToken(EmailRequest emailRequest) {
        logger.info("createOrUpdatePasswordResetToken() - Creating or updating password reset token by email "+emailRequest.email());
        Staff staff = staffRepo.findByEmail(emailRequest.email()).orElseThrow(() -> new EntityNotFoundException("Staff with email "+emailRequest.email()+" not found"));
        String token;
        if(staffHasToken(staff)){
           token = updatePasswordResetToken(staff);
            logger.info("createOrUpdatePasswordResetToken() - Password reset token was updated");
        } else {
            token = createPasswordResetToken(staff);
            logger.info("createOrUpdatePasswordResetToken() - Password reset token was created");

        }
        return token;
    }
    private boolean staffHasToken(Staff staff){
        return staff.getPasswordResetToken() != null;
    }
    private String updatePasswordResetToken(Staff staff) {
        String token = UUID.randomUUID().toString();
        staff.getPasswordResetToken().setToken(token);
        staff.getPasswordResetToken().setExpirationDate();
        staffRepo.save(staff);
        return token;
    }
    private String createPasswordResetToken(Staff staff) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token,staff);
        passwordResetTokenRepo.save(passwordResetToken);
        return token;
    }

    @Override
    public boolean isPasswordResetTokenValid(String token) {
        logger.info("isPasswordResetTokenValid() - Checking if password reset token "+token+" valid");
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepo.findByToken(token);
        boolean isValid = passwordResetToken.isPresent() && !passwordResetToken.get().getExpirationDate().isBefore(Instant.now());
        logger.info("isPasswordResetTokenValid() - Password reset token was checked");
        return isValid;
    }

    @Override
    public void updatePassword(String token, String password) {
        logger.info("updatePassword() - Updating password by password reset token "+token);
        PasswordResetToken passwordResetToken = passwordResetTokenRepo.findByToken(token).orElseThrow(()-> new EntityNotFoundException("Password reset token was not found by token "+token));
        passwordResetToken.getStaff().setPassword(passwordEncoder.encode(password));
        passwordResetTokenRepo.save(passwordResetToken);
        logger.info("updatePassword() - Password was updated");
    }
}
