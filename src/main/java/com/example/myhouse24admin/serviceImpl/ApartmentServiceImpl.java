package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Apartment;
import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.mapper.ApartmentMapper;
import com.example.myhouse24admin.model.apartments.ApartmentAddRequest;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import com.example.myhouse24admin.repository.ApartmentRepo;
import com.example.myhouse24admin.repository.PersonalAccountRepo;
import com.example.myhouse24admin.service.ApartmentService;
import com.example.myhouse24admin.specification.ApartmentSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        Pageable pageable = PageRequest.of(page, pageSize);
        searchParams.remove("page");
        searchParams.remove("size");
        ApartmentSpecification specification = new ApartmentSpecification(searchParams);
        Page<Apartment> all = apartmentRepo.findAll(specification, pageable);
        List<ApartmentResponse> apartmentResponses =
                apartmentMapper.apartmentListToApartmentResponseList(all.getContent());
        Page<ApartmentResponse> responsePage = new PageImpl<>(apartmentResponses, pageable, all.getTotalElements());
        return responsePage;
    }

    private void setPersonalAccountToApartment(Apartment apartment, ApartmentAddRequest apartmentAddRequest) {
        PersonalAccount personalAccount;
        if (apartmentAddRequest.getPersonalAccountId() != null) {
            personalAccount = new PersonalAccount();
            personalAccount.setId(apartmentAddRequest.getPersonalAccountId());
        } else if (apartmentAddRequest.getPersonalAccountNew() != null) {
            personalAccount = new PersonalAccount();
            personalAccount.setApartment(apartment);
            personalAccount.setStatus(PersonalAccountStatus.ACTIVE);
            personalAccount.setAccountNumber(apartmentAddRequest.getPersonalAccountNew());
        } else {
            personalAccount = new PersonalAccount();
            personalAccount.setApartment(apartment);
            personalAccount.setStatus(PersonalAccountStatus.ACTIVE);
            personalAccount.setAccountNumber(personalAccountRepo.getMaxAccountNumber());
        }
        apartment.setPersonalAccount(personalAccount);
    }
}
