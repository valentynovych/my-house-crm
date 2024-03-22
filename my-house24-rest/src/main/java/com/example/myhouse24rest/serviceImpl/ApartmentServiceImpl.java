package com.example.myhouse24rest.serviceImpl;

import com.example.myhouse24rest.entity.Apartment;
import com.example.myhouse24rest.mapper.ApartmentMapper;
import com.example.myhouse24rest.model.apartment.ApartmentShortResponse;
import com.example.myhouse24rest.repository.ApartmentRepo;
import com.example.myhouse24rest.service.ApartmentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class ApartmentServiceImpl implements ApartmentService {

    private final ApartmentRepo apartmentRepo;
    private final ApartmentMapper apartmentMapper;
    private final Logger logger = LogManager.getLogger(ApartmentServiceImpl.class);

    public ApartmentServiceImpl(ApartmentRepo apartmentRepo, ApartmentMapper apartmentMapper) {
        this.apartmentRepo = apartmentRepo;
        this.apartmentMapper = apartmentMapper;
    }

    @Override
    public Page<ApartmentShortResponse> getAllApartments(int page, int pageSize, Principal principal) {
        logger.info("getAllApartments() - Page: {} and pageSize: {} and ownerEmail: {}", page, pageSize, principal.getName());
        Pageable pageable = Pageable.ofSize(pageSize).withPage(page);
        Page<Apartment> apartmentsPage = apartmentRepo.findAllByOwner_Email(principal.getName(), pageable);
        Page<ApartmentShortResponse> apartmentResponses = convertApartmentPageToApartmentResponsePage(apartmentsPage);
        logger.info("getAllApartments() - Page: {} and pageSize: {} and ownerEmail: {} converted", page, pageSize, principal.getName());
        return apartmentResponses;
    }

    private Page<ApartmentShortResponse> convertApartmentPageToApartmentResponsePage(Page<Apartment> apartmentsPage) {
        logger.info("convertApartmentPageToApartmentResponsePage() - start convert Page: {}", apartmentsPage);
        List<ApartmentShortResponse> apartmentResponses = apartmentMapper.apartmentListToApartmentShortResponseList(apartmentsPage.getContent());
        Page<ApartmentShortResponse> responsePage = new PageImpl<>(apartmentResponses, apartmentsPage.getPageable(), apartmentsPage.getTotalElements());
        logger.info("convertApartmentPageToApartmentResponsePage() - end convert Page: {} and totalElements: {}", responsePage, responsePage.getTotalElements());
        return responsePage;
    }
}
