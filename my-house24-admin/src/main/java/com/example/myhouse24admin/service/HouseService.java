package com.example.myhouse24admin.service;

import com.example.myhouse24admin.model.houses.*;
import com.example.myhouse24admin.model.meterReadings.HouseNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface HouseService {
    void addNewHouse(HouseAddRequest houseAddRequest);

    Page<HouseShortResponse> getHouses(int page, int pageSize, Map<String, String> searchParams);

    boolean deleteHouseById(Long houseId);

    HouseViewResponse getHouseById(Long houseId);

    HouseResponse getHouseResponseById(Long houseId);

    void editHouse(Long houseId, HouseEditRequest houseEditRequest);
    Page<HouseNameResponse> getHousesForSelect(SelectSearchRequest selectSearchRequest);
}
