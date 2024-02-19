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
import java.util.Optional;

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
        Optional<Apartment> byId = apartmentRepo.findById(apartmentId);
        Apartment apartment = byId.orElseThrow(() -> {
            logger.error("Apartment with Id: {} not found", apartmentId);
            return new EntityNotFoundException(String.format("Apartment with Id: %s not found", apartmentId));
        });
        ApartmentExtendResponse apartmentResponse = apartmentMapper.apartmentToApartmentExtendResponse(apartment);
        logger.info("getApartments() -> exit, return ApartmentExtendResponse");
        return apartmentResponse;
    }

    @Override
    public void updateApartment(Long apartmentId, ApartmentAddRequest apartmentRequest) {
        logger.info("updateApartment() -> start");
        if (apartmentRequest.getId() != null) {
            Optional<Apartment> byId = apartmentRepo.findById(apartmentId);
            Apartment apartment = byId.orElseThrow(() -> {
                logger.error("Apartment with id: {} not found", apartmentId);
                return new EntityNotFoundException(String.format("Apartment with id: %s not found", apartmentId));
            });
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
        PersonalAccount personalAccount;
        if (apartmentAddRequest.getPersonalAccountId() != null) {
            Long personalAccountId = apartmentAddRequest.getPersonalAccountId();
            Optional<PersonalAccount> byId = personalAccountRepo.findById(personalAccountId);
            personalAccount = byId.orElseThrow(() -> {
                logger.error("Apartment with id: {} not found", personalAccountId);
                return new EntityNotFoundException(String.format("Apartment with id: %s not found", personalAccountId));
            });
        } else if (apartmentAddRequest.getPersonalAccountNew() != null) {
            Optional<PersonalAccount> personalAccountByApartment =
                    personalAccountRepo.findPersonalAccountByApartment(apartment);
            if (personalAccountByApartment.isPresent()) {
                personalAccount = personalAccountByApartment.get();
                personalAccount.setAccountNumber(apartmentAddRequest.getPersonalAccountNew());
            } else {
                personalAccount = new PersonalAccount();
                personalAccount.setApartment(apartment);
                personalAccount.setStatus(PersonalAccountStatus.ACTIVE);
                personalAccount.setAccountNumber(apartmentAddRequest.getPersonalAccountNew());
            }

        } else {
            personalAccount = new PersonalAccount();
            personalAccount.setApartment(apartment);
            personalAccount.setStatus(PersonalAccountStatus.ACTIVE);
            personalAccount.setAccountNumber(personalAccountRepo.getMaxAccountNumber());
        }
        apartment.setPersonalAccount(personalAccount);
    }

    @Override
    public Page<ApartmentNumberResponse> getApartmentsForSelect(SelectSearchRequest selectSearchRequest, Long houseId, Long sectionId) {
        logger.info("getApartmentsForSelect - Getting apartment name responses for select " + selectSearchRequest.toString());
        Pageable pageable = PageRequest.of(selectSearchRequest.page()-1, 10);
        Page<Apartment> apartments = getFilteredApartmentsForSelect(selectSearchRequest, pageable, houseId, sectionId);
        List<ApartmentNumberResponse> apartmentNumberRespons = apartmentMapper.apartmentListToApartmentNameResponse(apartments.getContent());
        Page<ApartmentNumberResponse> apartmentNameResponsePage = new PageImpl<>(apartmentNumberRespons, pageable, apartments.getTotalElements());
        logger.info("getApartmentsForSelect - Apartment name responses were got");
        return apartmentNameResponsePage;
    }

    private Page<Apartment> getFilteredApartmentsForSelect(SelectSearchRequest selectSearchRequest, Pageable pageable, Long houseId, Long sectionId) {
        Specification<Apartment> apartmentSpecification = Specification.where(byDeleted()
                .and(byHouseId(houseId)).and(bySectionId(sectionId)));
        if(!selectSearchRequest.search().isEmpty()){
            apartmentSpecification = apartmentSpecification.and(byNumber(Integer.valueOf(selectSearchRequest.search())));
        }
        return apartmentRepo.findAll(apartmentSpecification, pageable);
    }
}
