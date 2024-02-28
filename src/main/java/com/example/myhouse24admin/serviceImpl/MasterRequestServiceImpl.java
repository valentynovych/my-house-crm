package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.MasterRequest;
import com.example.myhouse24admin.mapper.MasterRequestMapper;
import com.example.myhouse24admin.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24admin.repository.MasterRequestRepo;
import com.example.myhouse24admin.service.MasterRequestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

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
}
