package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.masterRequest.MasterRequestAddRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestEditRequest;
import com.example.myhouse24admin.model.masterRequest.MasterRequestResponse;
import com.example.myhouse24admin.model.masterRequest.MasterRequestTableResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface MasterRequestService {
    void addNewMasterRequest(MasterRequestAddRequest request);

    Page<MasterRequestTableResponse> getMasterRequests(int page, int pageSize, Map<String, String> searchParams);

    boolean deleteMasterRequestById(Long masterRequestId);

    MasterRequestResponse getMasterRequestById(Long masterRequestId);

    void updateMasterRequest(Long masterRequestId, MasterRequestEditRequest request);
}
