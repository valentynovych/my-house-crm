package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestTableResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface MasterRequestService {
    void addNewMasterRequest(MasterRequestAddRequest request);

    Page<MasterRequestTableResponse> getMasterRequests(int page, int pageSize, Map<String, String> searchParams);

    boolean deleteMasterRequestById(Long masterRequestId);
}
