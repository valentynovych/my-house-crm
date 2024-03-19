package com.example.myhouse24user.service;

import com.example.myhouse24user.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24user.model.masterRequest.MasterRequestTableResponse;
import org.springframework.data.domain.Page;

public interface MasterRequestService {
    Page<MasterRequestTableResponse> getMasterRequests(String name, int page, int pageSize);

    void addMasterRequest(MasterRequestAddRequest masterRequest, String name);

    void deleteMasterRequest(Long id);
}
