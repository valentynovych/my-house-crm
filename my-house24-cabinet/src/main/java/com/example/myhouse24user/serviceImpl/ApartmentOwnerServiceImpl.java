package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.mapper.ApartmentOwnerMapper;
import com.example.myhouse24user.model.authentication.RegistrationRequest;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import com.example.myhouse24user.service.ApartmentOwnerService;
import com.example.myhouse24user.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApartmentOwnerServiceImpl implements ApartmentOwnerService {
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private final ApartmentOwnerMapper apartmentOwnerMapper;
    private final UploadFileUtil uploadFileUtil;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LogManager.getLogger(ApartmentOwnerServiceImpl.class);

    public ApartmentOwnerServiceImpl(ApartmentOwnerRepo apartmentOwnerRepo,
                                     ApartmentOwnerMapper apartmentOwnerMapper,
                                     UploadFileUtil uploadFileUtil,
                                     PasswordEncoder passwordEncoder) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
        this.apartmentOwnerMapper = apartmentOwnerMapper;
        this.uploadFileUtil = uploadFileUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(RegistrationRequest registrationRequest) {
        logger.info("register() - Registering owner " + registrationRequest.toString());
        String avatar = uploadFileUtil.saveDefaultOwnerImage();
        String encodedPassword = passwordEncoder.encode(registrationRequest.password());
        String ownerId = createOwnerId();
        ApartmentOwner apartmentOwner = apartmentOwnerMapper
                .registrationRequestToApartmentOwner(registrationRequest, avatar, encodedPassword,
                        OwnerStatus.NEW, ownerId, "");
        apartmentOwnerRepo.save(apartmentOwner);
        logger.info("register() - Owner was registered");
    }

    private String createOwnerId() {
        if (isTableEmpty()) {
            return "00001";
        } else {
            return createNewOwnerId();
        }
    }

    private boolean isTableEmpty() {
        return apartmentOwnerRepo.count() == 0;
    }

    private String createNewOwnerId() {
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findLast();
        String ownerId = apartmentOwner.getOwnerId();
        Integer numberPart = Integer.valueOf(ownerId);
        numberPart += 1;
        String newOwnerId = "";
        for (int i = 0; i < 5 - numberPart.toString().length(); i++) {
            newOwnerId += "0";
        }
        return newOwnerId + numberPart;
    }

    @Override
    public ApartmentOwner findApartmentOwnerByEmail(String ownerEmail) {
        Optional<ApartmentOwner> byEmail = apartmentOwnerRepo.findByEmail(ownerEmail);
        ApartmentOwner apartmentOwner = byEmail.orElseThrow(() -> {
            logger.error("getMessagesByOwnerEmail() -> ApartmentOwner by email: {} - not found", ownerEmail);
            return new EntityNotFoundException(String.format("getMessagesByOwnerEmail() -> ApartmentOwner by email: %s - not found", ownerEmail));
        });
        return apartmentOwner;
    }
}
