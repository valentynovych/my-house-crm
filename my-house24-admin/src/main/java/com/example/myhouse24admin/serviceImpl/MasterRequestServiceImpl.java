package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.MasterRequest;
import com.example.myhouse24admin.entity.MasterRequestStatus;
import com.example.myhouse24admin.mapper.MasterRequestMapper;
import com.example.myhouse24admin.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestEditRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestResponse;
import com.example.myhouse24admin.model.masterRequest.MasterRequestTableResponse;
import com.example.myhouse24admin.repository.MasterRequestRepo;
import com.example.myhouse24admin.service.MasterRequestService;
import com.example.myhouse24admin.specification.MasterRequestSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MasterRequestServiceImpl implements MasterRequestService {

    private final MasterRequestRepo masterRequestRepo;
    private final MasterRequestMapper masterRequestMapper;
    private final Logger logger = LogManager.getLogger(MasterRequestServiceImpl.class);

    public MasterRequestServiceImpl(MasterRequestRepo masterRequestRepo, MasterRequestMapper masterRequestMapper) {
        this.masterRequestRepo = masterRequestRepo;
        this.masterRequestMapper = masterRequestMapper;
    }

    @Override
    public void addNewMasterRequest(MasterRequestAddRequest request) {
        logger.info("addNewMasterRequest() -> start");
        MasterRequest masterRequest = masterRequestMapper.masterRequestAddRequestToMasterRequest(request);
        MasterRequest save = masterRequestRepo.save(masterRequest);
        logger.info("addNewMasterRequest() -> end, success add new MasterRequest with id: {}", save.getId());
    }

    @Override
    public Page<MasterRequestTableResponse> getMasterRequests(int page, int pageSize, Map<String, String> searchParams) {
        logger.info("getMasterRequests() -> start");
        Page<MasterRequest> pageMasterRequestsByFilters =
                findPageMasterRequestsByFilters(page, pageSize, searchParams);
        List<MasterRequestTableResponse> responseList = masterRequestMapper
                .masterRequestListToMasterRequestTableResponseList(pageMasterRequestsByFilters.getContent());
        Page<MasterRequestTableResponse> responsePage =
                new PageImpl<>(responseList,
                        pageMasterRequestsByFilters.getPageable(),
                        pageMasterRequestsByFilters.getTotalElements());
        logger.info("getMasterRequests() -> end, return Page<MasterRequestTableResponse>");
        return responsePage;
    }

    @Override
    public boolean deleteMasterRequestById(Long masterRequestId) {
        logger.info("deleteMasterRequestById() -> start, with id: {}", masterRequestId);
        MasterRequest masterRequest = findMasterRequestById(masterRequestId);
        if (masterRequest.getStatus().equals(MasterRequestStatus.IN_PROGRESS)) {
            logger.error("deleteMasterRequestById() -> MasterRequest with id: {} in status IN_PROGRESS, " +
                    "cannot deleting", masterRequestId);
            return false;
        }
        masterRequestRepo.delete(masterRequest);
        logger.info("deleteMasterRequestById() -> end, success deleting with id: {}", masterRequestId);
        return true;
    }

    @Override
    public MasterRequestResponse getMasterRequestById(Long masterRequestId) {
        logger.info("getMasterRequestById() -> start, with id: {}", masterRequestId);
        MasterRequest masterRequestById = findMasterRequestById(masterRequestId);
        MasterRequestResponse response = masterRequestMapper.masterRequestToMasterRequestResponse(masterRequestById);
        logger.info("getMasterRequestById() -> end, return MasterRequestResponse");
        return response;
    }

    @Override
    public void updateMasterRequest(Long masterRequestId, MasterRequestEditRequest request) {
        logger.info("updateMasterRequest() -> start, with id: {}", masterRequestId);
        MasterRequest masterRequestById = findMasterRequestById(masterRequestId);
        masterRequestMapper.updateMasterRequestFromMasterRequestEditRequest(masterRequestById, request);
        masterRequestRepo.save(masterRequestById);
        logger.info("updateMasterRequest() -> end, success update MasterRequest with id: {}", masterRequestId);
    }

    private MasterRequest findMasterRequestById(Long masterRequestId) {
        logger.info("findMasterRequestById() -> start, with id: {}", masterRequestId);
        Optional<MasterRequest> byId = masterRequestRepo.findByIdAndDeletedIsFalse(masterRequestId);
        MasterRequest masterRequest = byId.orElseThrow(() -> {
            logger.error("findMasterRequestById() -> MasterRequest by id: {} not found", masterRequestId);
            return new EntityNotFoundException("MasterRequest by id: %s not found");
        });
        logger.info("findMasterRequestById() -> end, return MasterRequest");
        return masterRequest;
    }

    private Page<MasterRequest> findPageMasterRequestsByFilters(int page, int pageSize, Map<String, String> searchParams) {
        logger.info("findPageMasterRequestsByFilters() -> start, with parameters: {}", searchParams);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id", "visitDate"));
        searchParams.remove("page");
        searchParams.remove("pageSize");
        MasterRequestSpecification specification = new MasterRequestSpecification(searchParams);
        Page<MasterRequest> all = masterRequestRepo.findAll(specification, pageable);
        logger.info("findPageMasterRequestsByFilters() -> end, return page with elements count: {}", all.getNumberOfElements());
        return all;
    }
}
