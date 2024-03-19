package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.mapper.ApartmentMapper;
import com.example.myhouse24user.model.apartments.ApartmentShortResponse;
import com.example.myhouse24user.repository.ApartmentRepo;
import com.example.myhouse24user.service.ApartmentService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Page<ApartmentShortResponse> getOwnerApartments(String name, int page, int pageSize) {
        logger.info("getOwnerApartments() -> start, name: {}, page: {}, pageSize: {}", name, page, pageSize);
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Apartment> allApartments = findAllApartments(pageable, name);
        List<ApartmentShortResponse> apartmentShortResponses =
                apartmentMapper.apartmentListToApartmentShortResponseList(allApartments.getContent());
        Page<ApartmentShortResponse> response =
                new PageImpl<>(apartmentShortResponses, pageable, allApartments.getTotalElements());
        logger.info("getOwnerApartments() -> end, response elements: {}", response.getNumberOfElements());
        return response;
    }

    @Override
    public Apartment findApartmentByIdAndOwner(Long apartmentId, String name) {
        Optional<Apartment> apartmentByIdAndOwnerEmail = apartmentRepo.findApartmentByIdAndOwner_Email(apartmentId, name);
        Apartment apartment = apartmentByIdAndOwnerEmail.orElseThrow(() -> {
            logger.error("findApartmentByIdAndOwner() -> apartment not found, apartmentId: {}, name: {}", apartmentId, name);
            return new EntityNotFoundException("Apartment by id " + apartmentId + ", and owner " + name + " not found");
        });
        return apartment;
    }

    private Page<Apartment> findAllApartments(Pageable pageable, String ownerEmail) {
        logger.info("findAllApartments() -> start, ownerEmail: {}, pageable: {}", ownerEmail, pageable);
        Page<Apartment> allByOwnerEmail = apartmentRepo.findAllByOwner_Email(ownerEmail, pageable);
        logger.info("findAllApartments() -> end, allByOwnerEmail elements: {}", allByOwnerEmail.getNumberOfElements());
        return allByOwnerEmail;
    }
}
