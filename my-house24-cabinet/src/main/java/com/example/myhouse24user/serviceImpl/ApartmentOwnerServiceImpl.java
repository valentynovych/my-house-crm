package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.entity.OwnerStatus;
import com.example.myhouse24user.mapper.ApartmentMapper;
import com.example.myhouse24user.mapper.ApartmentOwnerMapper;
import com.example.myhouse24user.model.authentication.RegistrationRequest;
import com.example.myhouse24user.model.owner.ApartmentOwnerRequest;
import com.example.myhouse24user.model.owner.ApartmentResponse;
import com.example.myhouse24user.model.owner.EditOwnerResponse;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

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

    @Override
    public ViewOwnerResponse getViewOwnerResponse() {
        logger.info("getViewOwnerResponse() - Getting view owner response");
        String email = getLoggedOwnerEmail();
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findByEmail(email).orElseThrow(()-> new EntityNotFoundException("Owner was not found by email "+email));
        List<Apartment> apartments = apartmentRepo.findAll(byOwnerEmail(email));
        List<ApartmentResponse> apartmentResponses = apartmentMapper.apartmentListToApartmentResponseList(apartments);
        ViewOwnerResponse viewOwnerResponse = apartmentOwnerMapper
                .ownerToViewOwnerResponse(apartmentOwner, apartmentResponses);
        logger.info("getViewOwnerResponse() - View owner response was got");
        return viewOwnerResponse;
    }

    @Override
    public EditOwnerResponse getEditOwnerResponse() {
        logger.info("getViewOwnerResponse() - Getting edit owner response");
        String email = getLoggedOwnerEmail();
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findByEmail(email).orElseThrow(()-> new EntityNotFoundException("Owner was not found by email "+email));
        EditOwnerResponse editOwnerResponse = apartmentOwnerMapper.ownerToEditOwnerResponse(apartmentOwner);
        logger.info("getEditOwnerResponse() - Edit owner response was got");
        return editOwnerResponse;
    }
    private String getLoggedOwnerEmail(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String email = userDetails.getUsername();
        return email;
    }

    @Override
    public void updateProfile(ApartmentOwnerRequest apartmentOwnerRequest, MultipartFile multipartFile) {
        logger.info("updateProfile() - Updating owner profile");
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findById(apartmentOwnerRequest.id()).orElseThrow(() -> new EntityNotFoundException("Owner not found by id " + apartmentOwnerRequest.id()));
        if (apartmentOwnerRequest.password().isEmpty()) {
            apartmentOwnerMapper.setApartmentOwnerWithoutPassword(apartmentOwner, apartmentOwnerRequest);
        } else {
            apartmentOwnerMapper.setApartmentOwnerWithPassword(apartmentOwner, apartmentOwnerRequest, passwordEncoder.encode(apartmentOwnerRequest.password()));
        }
        updateImage(multipartFile, apartmentOwner);
        apartmentOwnerRepo.save(apartmentOwner);
        logger.info("updateProfile() - Owner profile was updated");
    }
    private void updateImage(MultipartFile multipartFile, ApartmentOwner apartmentOwner) {
        if (multipartFile != null) {
            String createdImageName = uploadFileUtil.saveMultipartFile(multipartFile);
            apartmentOwner.setAvatar(createdImageName);
        }
    }
}
