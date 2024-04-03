package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.mapper.ApartmentMapper;
import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.model.apartments.ApartmentExtendResponse;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import com.example.myhouse24admin.model.meterReadings.ApartmentNumberResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.PersonalAccountRepo;
import com.example.myhouse24admin.service.ApartmentService;
import com.example.myhouse24admin.specification.ApartmentSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.example.myhouse24admin.specification.ApartmentInterfaceSpecification.*;

@Service
public class ApartmentServiceImpl implements ApartmentService {

    private final ApartmentRepo apartmentRepo;
    private final ApartmentMapper apartmentMapper;
    private final PersonalAccountRepo personalAccountRepo;
    private final Logger logger = LogManager.getLogger(ApartmentServiceImpl.class);

    public ApartmentServiceImpl(ApartmentRepo apartmentRepo, ApartmentMapper apartmentMapper, PersonalAccountRepo personalAccountRepo) {
        this.apartmentRepo = apartmentRepo;
        this.apartmentMapper = apartmentMapper;
        this.personalAccountRepo = personalAccountRepo;
    }

    @Override
    public void addNewApartment(ApartmentAddRequest apartmentAddRequest) {
        logger.info("addNewApartment() -> start");
        Apartment apartment = apartmentMapper.apartmentAddRequestToApartment(apartmentAddRequest);
        logger.info("addNewApartment() -> set PersonalAccount to Apartment");
        setPersonalAccountToApartment(apartment, apartmentAddRequest);
        Apartment save = apartmentRepo.save(apartment);
        logger.info("addNewApartment() -> success save new Apartment with id: {}", save.getId());
    }

    @Override
    public Page<ApartmentResponse> getApartments(int page, int pageSize, Map<String, String> searchParams) {
        logger.info("getApartments() -> start with parameters: {}", searchParams);
        Pageable pageable = PageRequest.of(page, pageSize);
        searchParams.remove("page");
        searchParams.remove("size");
        ApartmentSpecification specification = new ApartmentSpecification(searchParams);
        Page<Apartment> all = apartmentRepo.findAll(specification, pageable);
        List<ApartmentResponse> apartmentResponses =
                apartmentMapper.apartmentListToApartmentResponseList(all.getContent());
        Page<ApartmentResponse> responsePage = new PageImpl<>(apartmentResponses, pageable, all.getTotalElements());
        logger.info("getApartments() -> exit, return page elements: {}", all.getNumberOfElements());
        return responsePage;
    }

    @Override
    public ApartmentExtendResponse getApartmentById(Long apartmentId) {
        logger.info("getApartments() -> start with id: {}", apartmentId);
        Apartment apartment = findApartmentById(apartmentId);
        ApartmentExtendResponse apartmentResponse = apartmentMapper.apartmentToApartmentExtendResponse(apartment);
        logger.info("getApartments() -> exit, return ApartmentExtendResponse");
        return apartmentResponse;
    }

    @Override
    public void updateApartment(Long apartmentId, ApartmentAddRequest apartmentRequest) {
        logger.info("updateApartment() -> start");
        if (apartmentRequest.getId() != null) {
            Apartment apartment = findApartmentById(apartmentId);
            apartmentMapper.updateApartmentFromApartmentRequest(apartment, apartmentRequest);
            logger.info("updateApartment() -> set PersonalAccount to Apartment");
            setPersonalAccountToApartment(apartment, apartmentRequest);
            apartmentRepo.save(apartment);
            logger.info("updateApartment() -> success update Apartment with id: {}", apartmentId);
        } else {
            logger.error("Apartment with id: null not found");
            throw new IllegalStateException("Apartment id is null");
        }
        logger.info("updateApartment() -> success update Apartment with id: " + apartmentId);
    }

    private void setPersonalAccountToApartment(Apartment apartment, ApartmentAddRequest apartmentAddRequest) {
        logger.info("setPersonalAccountToApartment() -> start");
        PersonalAccount personalAccount = getPersonalAccount(apartmentAddRequest);
        updateApartmentPersonalAccount(apartment, personalAccount);
        logger.info("setPersonalAccountToApartment() -> success");
    }

    private PersonalAccount getPersonalAccount(ApartmentAddRequest apartmentAddRequest) {
        if (apartmentAddRequest.getPersonalAccountId() != null) {
            logger.info("Getting existing personal account for id: " + apartmentAddRequest.getPersonalAccountId());
            return getExistingPersonalAccount(apartmentAddRequest.getPersonalAccountId());
        } else if (apartmentAddRequest.getPersonalAccountNew() != null) {
            logger.info("Creating new personal account");
            return getOrCreateNewPersonalAccount(apartmentAddRequest.getPersonalAccountNew());
        } else {
            logger.info("Creating new personal account with minimal free account number");
            return getOrCreateNewPersonalAccount(personalAccountRepo.findMinimalFreeAccountNumber());
        }
    }

    private PersonalAccount getExistingPersonalAccount(Long personalAccountId) {
        logger.info("Getting personal account with id: {}", personalAccountId);
        return personalAccountRepo.findById(personalAccountId)
                .orElseThrow(() -> {
                    logger.error("Apartment with id: {} not found", personalAccountId);
                    return new EntityNotFoundException(String.format("Apartment with id: %s not found", personalAccountId));
                });
    }

    private PersonalAccount getOrCreateNewPersonalAccount(Long accountNumber) {
        logger.info("Creating new personal account with account number: {}", accountNumber);
        PersonalAccount personalAccount = new PersonalAccount();
        personalAccount.setStatus(PersonalAccountStatus.ACTIVE);
        personalAccount.setAccountNumber(accountNumber);
        logger.info("Created new personal account with id: {}", personalAccount.getId());
        return personalAccount;
    }

    private void updateApartmentPersonalAccount(Apartment apartment, PersonalAccount personalAccount) {
        logger.info("updateApartmentPersonalAccount() -> start");
        if (apartment.getPersonalAccount() != null
                && !apartment.getPersonalAccount().getId().equals(personalAccount.getId())) {
            PersonalAccount toDeleteApartmentId = apartment.getPersonalAccount();
            toDeleteApartmentId.setApartment(null);
            personalAccountRepo.save(toDeleteApartmentId);
            logger.info("Deleting personal account with id: {}", toDeleteApartmentId.getId());
        }
        personalAccount.setApartment(apartment);
        apartment.setPersonalAccount(personalAccount);
        logger.info("Updated personal account for apartment: {}", apartment.getId());
    }

    @Override
    public Page<ApartmentNumberResponse> getApartmentsForSelect(SelectSearchRequest selectSearchRequest, Long houseId, Long sectionId) {
        logger.info("getApartmentsForSelect - Getting apartment name responses for select " + selectSearchRequest.toString());
        Pageable pageable = PageRequest.of(selectSearchRequest.page() - 1, 10);
        Page<Apartment> apartments = getFilteredApartmentsForSelect(selectSearchRequest, pageable, houseId, sectionId);
        List<ApartmentNumberResponse> apartmentNumberResponses = apartmentMapper.apartmentListToApartmentNameResponse(apartments.getContent());
        Page<ApartmentNumberResponse> apartmentNameResponsePage = new PageImpl<>(apartmentNumberResponses, pageable, apartments.getTotalElements());
        logger.info("getApartmentsForSelect - Apartment name responses were got");
        return apartmentNameResponsePage;
    }

    @Override
    public List<Apartment> getAllApartmentsBy(Pageable pageable,
                                              List<Apartment> apartments,
                                              ApartmentSpecification specification) {
        logger.info("getAllApartmentsBy() -> Fetching all apartments by specification: {}", specification.toString());
        Page<Apartment> all = apartmentRepo.findAll(specification, pageable);
        apartments.addAll(all.getContent());
        if (all.hasNext()) {
            logger.info("getAllApartmentsBy() -> Fetching next page of apartments by specification: {}", specification.toString());
            getAllApartmentsBy(pageable.next(), apartments, specification);
        }
        logger.info("getAllApartmentsBy() -> Fetching all apartments by specification: {}", specification.toString());
        return apartments;
    }

    private Page<Apartment> getFilteredApartmentsForSelect(SelectSearchRequest selectSearchRequest,
                                                           Pageable pageable,
                                                           Long houseId, Long sectionId) {
        logger.info("getFilteredApartmentsForSelect() -> Fetching filtered apartments for select");
        Specification<Apartment> apartmentSpecification = Specification.where(byDeleted()
                .and(byHouseId(houseId)));
        if (!selectSearchRequest.search().isEmpty()) {
            logger.info("getFilteredApartmentsForSelect() -> Applying additional search criteria: {}", selectSearchRequest.search());
            apartmentSpecification = apartmentSpecification.and(byNumberLike(selectSearchRequest.search()));
        }
        if (sectionId != null) {
            apartmentSpecification = apartmentSpecification.and(bySectionId(sectionId));
        }
        return apartmentRepo.findAll(apartmentSpecification, pageable);
    }

    private Apartment findApartmentById(Long apartmentId) {
        logger.info("findApartmentById() -> Fetching apartment with id: {}", apartmentId);
        return apartmentRepo.findById(apartmentId)
                .orElseThrow(() -> {
                    logger.error("Apartment with id: {} not found", apartmentId);
                    return new EntityNotFoundException(String.format("Apartment with id: %s not found", apartmentId));
                });
    }
}
