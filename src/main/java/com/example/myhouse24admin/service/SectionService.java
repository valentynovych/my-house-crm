package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.houses.SectionResponse;
import org.springframework.data.domain.Page;

public interface SectionService {
    Page<SectionResponse> getSectionsByHouseId(Long houseId, int page, int pageSize, String name);
}
