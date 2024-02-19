package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.houses.SectionResponse;
import com.example.myhouse24admin.model.meterReadings.SectionNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import org.springframework.data.domain.Page;

public interface SectionService {
    Page<SectionResponse> getSectionsByHouseId(Long houseId, int page, int pageSize, String name);
    Page<SectionNameResponse> getSectionForSelect(SelectSearchRequest selectSearchRequest,
                                                  Long houseId);
}
