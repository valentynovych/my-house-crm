package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.houses.FloorResponse;
import org.springframework.data.domain.Page;

public interface FloorService {
    Page<FloorResponse> getFloorsByHouseId(Long houseId, int page, int pageSize, String name);
}
