package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.MasterRequest;
import com.example.myhouse24user.entity.MasterRequestStatus;
import com.example.myhouse24user.mapper.MasterRequestMapper;
import com.example.myhouse24user.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24user.model.masterRequest.MasterRequestTableResponse;
import com.example.myhouse24user.repository.MasterRequestRepo;
import com.example.myhouse24user.service.ApartmentService;
import com.example.myhouse24user.service.MasterRequestService;
import com.example.myhouse24user.specification.MasterRequestSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasterRequestServiceImpl implements MasterRequestService {

    private final MasterRequestRepo masterRequestRepo;
    private final MasterRequestMapper masterRequestMapper;
    private final ApartmentService apartmentService;
    private final Logger logger = LogManager.getLogger(MasterRequestServiceImpl.class);

    public MasterRequestServiceImpl(MasterRequestRepo masterRequestRepo, MasterRequestMapper masterRequestMapper, ApartmentService apartmentService) {
        this.masterRequestRepo = masterRequestRepo;
        this.masterRequestMapper = masterRequestMapper;
        this.apartmentService = apartmentService;
    }

    @Override
    public Page<MasterRequestTableResponse> getMasterRequests(String name, int page, int pageSize) {
        logger.info("getMasterRequests() -> start, name: {}, page: {}, pageSize: {}", name, page, pageSize);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "creationDate"));
        Page<MasterRequest> allMasterRequests = findAllMasterRequests(pageable, name);
        List<MasterRequestTableResponse> tableResponses =
                masterRequestMapper.masterRequestListToMasterRequestTableResponseList(allMasterRequests.getContent());
        Page<MasterRequestTableResponse> response =
                new PageImpl<>(tableResponses, pageable, allMasterRequests.getTotalElements());
        logger.info("getMasterRequests() -> end, response elements: {}", response.getNumberOfElements());
        return response;
    }

    @Override
    public void addMasterRequest(MasterRequestAddRequest masterRequest, String name) {
        logger.info("addMasterRequest() -> start, masterRequest: {}, name: {}", masterRequest, name);
        Apartment apartment = apartmentService.findApartmentByIdAndOwner(masterRequest.getApartmentId(), name);
        MasterRequest masterRequestEntity = masterRequestMapper.masterRequestAddRequestToMasterRequest(masterRequest, apartment);
        masterRequestRepo.save(masterRequestEntity);
        logger.info("addMasterRequest() -> end");
    }

    @Override
    public void deleteMasterRequest(Long id) {
        logger.info("deleteMasterRequest() -> start, id: {}", id);
        MasterRequest masterRequestById = findMasterRequestById(id);
        masterRequestById.setStatus(MasterRequestStatus.CANCELED);
        masterRequestRepo.save(masterRequestById);
        logger.info("deleteMasterRequest() -> end, mark as CANCELED");
    }

    private Page<MasterRequest> findAllMasterRequests(Pageable pageable, String ownerEmail) {
        logger.info("findAllMasterRequests() -> start, ownerEmail: {}, pageable: {}", ownerEmail, pageable);
        Specification<MasterRequest> specification = MasterRequestSpecification.byApartmentOwnerEmail(ownerEmail);
        Specification<MasterRequest> byNotStatus = MasterRequestSpecification.byNotStatus(MasterRequestStatus.CANCELED);
        Page<MasterRequest> masterRequests = masterRequestRepo.findAll(specification.and(byNotStatus), pageable);
        logger.info("findAllMasterRequests() -> end, masterRequests elements: {}", masterRequests.getNumberOfElements());
        return masterRequests;
    }

    private MasterRequest findMasterRequestById(Long id) {
        logger.info("findMasterRequestById() -> start, id: {}", id);
        MasterRequest masterRequest = masterRequestRepo.findById(id).orElseThrow(() -> {
            logger.error("findMasterRequestById() -> masterRequest not found, id: {}", id);
            return new EntityNotFoundException("Master request by id " + id + " not found");
        });
        logger.info("findMasterRequestById() -> end");
        return masterRequest;
    }
}
