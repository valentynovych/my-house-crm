package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Floor;
import com.example.myhouse24admin.mapper.FloorMapper;
import com.example.myhouse24admin.model.houses.FloorResponse;
import com.example.myhouse24admin.repository.FloorRepo;
import com.example.myhouse24admin.service.FloorService;
import com.example.myhouse24admin.specification.FloorSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FloorServiceImpl implements FloorService {

    private final FloorRepo floorRepo;
    private final FloorMapper floorMapper;
    private final Logger logger = LogManager.getLogger(FloorServiceImpl.class);

    public FloorServiceImpl(FloorRepo floorRepo, FloorMapper floorMapper) {
        this.floorRepo = floorRepo;
        this.floorMapper = floorMapper;
    }

    @Override
    public Page<FloorResponse> getFloorsByHouseId(Long houseId, int page, int pageSize, String name) {
        logger.info("getFloorsByHouseId() -> start with parameters: {}, {}, {}", houseId, page, pageSize);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("name").ascending());
        FloorSpecification specification =
                new FloorSpecification(Map.of("name", name, "houseId", houseId.toString()));
        Page<Floor> all = floorRepo.findAll(specification, pageable);
        List<FloorResponse> floorResponseList = floorMapper.floorListToFloorResponseList(all.getContent());
        Page<FloorResponse> responsePage = new PageImpl<>(floorResponseList, pageable, all.getTotalElements());
        logger.info("getFloorsByHouseId() -> success with parameters: {}, {}, {}", houseId, page, pageSize);
        return responsePage;
    }

    @Override
    public void deleteFloorsByHouseId(Long houseId) {
        logger.info("deleteFloorsByHouseId() - Deleting floors by house id "+houseId);
        FloorSpecification specification =
                new FloorSpecification(Map.of("houseId", houseId.toString()));
        List<Floor> floors = floorRepo.findAll(specification);
        for(Floor floor: floors){
            floor.setDeleted(true);
        }
        floorRepo.saveAll(floors);
        logger.info("deleteFloorsByHouseId() - Floors were deleted");
    }
}
