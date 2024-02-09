package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.ApartmentOwner;
import com.example.myhouse24admin.mapper.ApartmentOwnerMapper;
import com.example.myhouse24admin.model.apartmentOwner.*;
import com.example.myhouse24admin.repository.ApartmentOwnerRepo;
import com.example.myhouse24admin.service.ApartmentOwnerService;
import com.example.myhouse24admin.service.MailService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import static com.example.myhouse24admin.specification.ApartmentOwnerSpecification.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApartmentOwnerServiceImpl implements ApartmentOwnerService {
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private final ApartmentOwnerMapper apartmentOwnerMapper;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final ResourceLoader resourceLoader;
    private final Logger logger = LogManager.getLogger(ApartmentOwnerServiceImpl.class);
    private String uploadPath = "C:\\Users\\Anastassia\\IdeaProjects\\MyHouse24-Admin\\uploads";

    public ApartmentOwnerServiceImpl(ApartmentOwnerRepo apartmentOwnerRepo,
                                     ApartmentOwnerMapper apartmentOwnerMapper,
                                     PasswordEncoder passwordEncoder,
                                     MailService mailService,
                                     ResourceLoader resourceLoader) {
        this.apartmentOwnerRepo = apartmentOwnerRepo;
        this.apartmentOwnerMapper = apartmentOwnerMapper;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void createApartmentOwner(CreateApartmentOwnerRequest createApartmentOwnerRequest, MultipartFile avatar) {
        logger.info("createApartmentOwner - Creating apartment owner");
        String savedImageName = saveImage(avatar);
        ApartmentOwner apartmentOwner = apartmentOwnerMapper.apartmentOwnerRequestToApartmentOwner(createApartmentOwnerRequest, passwordEncoder.encode(createApartmentOwnerRequest.password()), savedImageName);
        apartmentOwnerRepo.save(apartmentOwner);
        logger.info("createApartmentOwner - Apartment owner was created");
    }
    private String saveImage(MultipartFile multipartFile){
        createUploadDirectoryIfNotExist();
        if(multipartFile == null){
            return saveDefaultImage();
        }
        return saveMultipartFileToDirectory(multipartFile);
    }

    private String saveDefaultImage() {
        File file = new File(uploadPath+"\\defaultAvatar.png");
        Resource resource = resourceLoader.getResource("classpath:static/assets/img/avatars/1.png");
        try {
            InputStream stream = resource.getInputStream();
            FileUtils.copyInputStreamToFile(stream, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "defaultAvatar.png";
    }


    @Override
    public ApartmentOwnerResponse getApartmentOwnerResponse(Long id) {
        logger.info("getApartmentOwnerResponse - Getting apartment owner response by id "+id);
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Owner not found by id "+id));
        ApartmentOwnerResponse apartmentOwnerResponse = apartmentOwnerMapper.apartmentOwnerToApartmentOwnerResponse(apartmentOwner);
        logger.info("getApartmentOwnerResponse - Apartment owner response was got");
        return apartmentOwnerResponse;
    }

    @Override
    public void updateApartmentOwner(EditApartmentOwnerRequest editApartmentOwnerRequest, Long id, MultipartFile multipartFile) {
        logger.info("updateApartmentOwner - Updating apartment owner with id "+id);
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Owner not found by id "+id));
        if(editApartmentOwnerRequest.password().isEmpty()) {
            apartmentOwnerMapper.setApartmentOwnerWithoutPassword(apartmentOwner, editApartmentOwnerRequest);
        } else {
            apartmentOwnerMapper.setApartmentOwnerWithPassword(apartmentOwner, editApartmentOwnerRequest, passwordEncoder.encode(editApartmentOwnerRequest.password()));
            mailService.sendNewPassword(editApartmentOwnerRequest.email(),editApartmentOwnerRequest.password());

        }
        updateImage(multipartFile, apartmentOwner);
        apartmentOwnerRepo.save(apartmentOwner);
        logger.info("updateApartmentOwner - Apartment owner was updated");
    }

    private void updateImage(MultipartFile multipartFile, ApartmentOwner apartmentOwner) {
        if(multipartFile != null) {
            createUploadDirectoryIfNotExist();
            String createdImageName = saveMultipartFileToDirectory(multipartFile);
            apartmentOwner.setAvatar(createdImageName);
        }
    }

    @Override
    public Page<TableApartmentOwnerResponse> getApartmentOwnerResponsesForTable(int page, int pageSize, FilterRequest filterRequest) {
        logger.info("getApartmentOwnerResponsesForTable - Getting apartment owner responses for table with filters "+filterRequest.toString());
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ApartmentOwner> apartmentOwners = getFilteredOwners(pageable, filterRequest);
        List<TableApartmentOwnerResponse> apartmentOwnerResponseList = apartmentOwnerMapper.apartmentOwnerListToTableApartmentOwnerResponseList(apartmentOwners.getContent());
        Page<TableApartmentOwnerResponse> apartmentOwnerResponsePage = new PageImpl<>(apartmentOwnerResponseList, pageable, apartmentOwners.getTotalElements());
        logger.info("getApartmentOwnerResponsesForTable - Apartment owner responses were got");
        return apartmentOwnerResponsePage;
    }

    private Page<ApartmentOwner> getFilteredOwners(Pageable pageable, FilterRequest filterRequest) {
        Specification<ApartmentOwner> ownerSpecification = Specification.where(byDeleted());
        if(filterRequest.id() != null){
            ownerSpecification = ownerSpecification.and(byId(filterRequest.id()));
        }
        if(!filterRequest.phoneNumber().isEmpty()){
            ownerSpecification = ownerSpecification.and(byPhoneNumber(filterRequest.phoneNumber()));
        }
        if(!filterRequest.fullName().isEmpty()){
            ownerSpecification = ownerSpecification.and(byFirstName(filterRequest.fullName())
                    .or(byLastName(filterRequest.fullName()))
                    .or(byMiddleName(filterRequest.fullName())));
        }
        if(!filterRequest.email().isEmpty()){
            ownerSpecification = ownerSpecification.and(byEmail(filterRequest.email()));
        }
        if(!filterRequest.creationDate().isEmpty()){
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.parse(filterRequest.creationDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    LocalTime.MIDNIGHT);
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant dateFrom = zonedDateTime.toInstant();
            Instant dateTo = zonedDateTime.toInstant().plus(1, ChronoUnit.DAYS);
            ownerSpecification = ownerSpecification.and(byCreationDateGreaterThan(dateFrom)).and(byCreationDateLessThan(dateTo));
        }
        if(filterRequest.status() != null){
            ownerSpecification = ownerSpecification.and(byStatus(filterRequest.status()));
        }
        return apartmentOwnerRepo.findAll(ownerSpecification, pageable);
    }

    @Override
    public void deleteOwnerById(Long id) {
        logger.info("deleteOwnerById - Deleting apartment owner by id "+id);
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Owner was not found with id "+id));
        apartmentOwner.setDeleted(true);
        apartmentOwnerRepo.save(apartmentOwner);
        logger.info("deleteOwnerById - Apartment owner was deleted");
    }
    @Override
    public ViewApartmentOwnerResponse getApartmentOwnerResponseForView(Long id) {
        logger.info("getApartmentOwnerResponseForView - Getting apartment owner response for view by id "+id);
        ApartmentOwner apartmentOwner = apartmentOwnerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Owner was not found by id "+id));
        ViewApartmentOwnerResponse viewApartmentOwnerResponse = apartmentOwnerMapper.apartmentOwnerToViewApartmentOwnerResponse(apartmentOwner);
        logger.info("getApartmentOwnerResponseForView - Apartment owner response for view was got");
        return viewApartmentOwnerResponse;
    }


    void createUploadDirectoryIfNotExist(){
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()){
            uploadDir.mkdir();
        }
    }

    private String saveMultipartFileToDirectory(MultipartFile multipartFile){
        String uuidFile = UUID.randomUUID().toString();
        String uniqueName = uuidFile + "." + multipartFile.getOriginalFilename();
        Path path = Paths.get(uploadPath + "/" + uniqueName);
        try {
            multipartFile.transferTo(new File(path.toUri()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return uniqueName;
    }
}
