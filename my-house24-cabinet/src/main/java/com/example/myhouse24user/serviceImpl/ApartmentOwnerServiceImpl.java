package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.mapper.ApartmentMapper;
import com.example.myhouse24user.mapper.ApartmentOwnerMapper;
import com.example.myhouse24user.model.authentication.RegistrationRequest;
import com.example.myhouse24user.model.owner.ApartmentResponse;
import com.example.myhouse24user.model.owner.ViewOwnerResponse;
import com.example.myhouse24user.repository.ApartmentOwnerRepo;
import com.example.myhouse24user.repository.ApartmentRepo;
import com.example.myhouse24user.service.ApartmentOwnerService;
import com.example.myhouse24user.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.myhouse24user.specification.ApartmentSpecification.byOwnerEmail;

@Service
public class ApartmentOwnerServiceImpl implements ApartmentOwnerService {
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private final ApartmentRepo apartmentRepo;
    private final ApartmentOwnerMapper apartmentOwnerMapper;
    private final ApartmentMapper apartmentMapper;
    private final UploadFileUtil uploadFileUtil;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LogManager.getLogger(ApartmentOwnerServiceImpl.class);

    public ApartmentOwnerServiceImpl(ApartmentOwnerRepo apartmentOwnerRepo,
                                     ApartmentRepo apartmentRepo,
                                     ApartmentOwnerMapper apartmentOwnerMapper,
                                     ApartmentMapper apartmentMapper,
                                     UploadFileUtil uploadFileUtil,
                                     PasswordEncoder passwordEncoder) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
        this.apartmentRepo = apartmentRepo;
        this.apartmentOwnerMapper = apartmentOwnerMapper;
        this.apartmentMapper = apartmentMapper;
        this.uploadFileUtil = uploadFileUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(RegistrationRequest registrationRequest) {
        logger.info("register() - Registering owner "+registrationRequest.toString());
        String avatar = uploadFileUtil.saveDefaultOwnerImage();
        String encodedPassword = passwordEncoder.encode(registrationRequest.password());
        String ownerId = createOwnerId();
        ApartmentOwner apartmentOwner = apartmentOwnerMapper
                .registrationRequestToApartmentOwner(registrationRequest, avatar, encodedPassword,
                        OwnerStatus.NEW, ownerId,"");
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
    public ViewOwnerResponse getViewOwnerResponse() {
        logger.info("getViewOwnerResponse() - Getting view owner response");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String email = userDetails.getUsername();
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findByEmail(email).orElseThrow(()-> new EntityNotFoundException("Owner was not found by email "+email));
        List<Apartment> apartments = apartmentRepo.findAll(byOwnerEmail(email));
        List<ApartmentResponse> apartmentResponses = apartmentMapper.apartmentListToApartmentResponseList(apartments);
        ViewOwnerResponse viewOwnerResponse = apartmentOwnerMapper
                .ownerToViewOwnerResponse(apartmentOwner, apartmentResponses);
        logger.info("getViewOwnerResponse() - View owner response was got");
        return viewOwnerResponse;
    }
}
