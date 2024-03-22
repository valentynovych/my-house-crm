package com.example.myhouse24rest.serviceImpl;

import com.example.myhouse24rest.entity.ApartmentOwner;
import com.example.myhouse24rest.mapper.ApartmentOwnerMapper;
import com.example.myhouse24rest.model.profile.ProfileResponse;
import com.example.myhouse24rest.repository.ApartmentOwnerRepo;
import com.example.myhouse24rest.service.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private final ApartmentOwnerMapper apartmentOwnerMapper;
    private final Logger logger = LogManager.getLogger(ProfileServiceImpl.class);

    public ProfileServiceImpl(ApartmentOwnerRepo apartmentOwnerRepo, ApartmentOwnerMapper apartmentOwnerMapper) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
        this.apartmentOwnerMapper = apartmentOwnerMapper;
    }

    @Override
    public ProfileResponse getProfile(Principal principal) {
        logger.info("getProfile() -> Looking for owner: {}", principal.getName());
        ApartmentOwner byEmail = getApartmentOwner(principal);
        ProfileResponse profileResponse = apartmentOwnerMapper.apartmentOwnerToProfileResponse(byEmail);
        logger.info("getProfile() -> Owner found: {}", principal.getName());
        return profileResponse;
    }

    private ApartmentOwner getApartmentOwner(Principal principal) {
        logger.info("Looking for owner: {}", principal.getName());
        Optional<ApartmentOwner> byEmail = apartmentOwnerRepo.findByEmail(principal.getName());
        ApartmentOwner apartmentOwner = byEmail.orElseThrow(() -> {
            logger.error("Owner not found: {}", principal.getName());
            return new EntityNotFoundException("Owner not found: " + principal.getName());
        });
        logger.info("Owner found: {}", principal.getName());
        return apartmentOwner;
    }
}
