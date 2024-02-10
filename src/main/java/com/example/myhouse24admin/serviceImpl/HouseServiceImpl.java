package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.House;
import com.example.myhouse24admin.mapper.HouseMapper;
import com.example.myhouse24admin.model.houses.HouseAddRequest;
import com.example.myhouse24admin.model.houses.HouseViewResponse;
import com.example.myhouse24admin.model.houses.HouseShortResponse;
import com.example.myhouse24admin.repository.HouseRepo;
import com.example.myhouse24admin.service.HouseService;
import com.example.myhouse24admin.specification.HouseSpecification;
import com.example.myhouse24admin.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HouseServiceImpl implements HouseService {

    private final HouseRepo houseRepo;
    private final HouseMapper houseMapper;
    private final UploadFileUtil fileUtil;
    private final Logger logger = LogManager.getLogger(HouseServiceImpl.class);

    public HouseServiceImpl(HouseRepo houseRepo, HouseMapper houseMapper, UploadFileUtil fileUtil) {
        this.houseRepo = houseRepo;
        this.houseMapper = houseMapper;
        this.fileUtil = fileUtil;
    }

    @Override
    public void addNewHouse(HouseAddRequest houseAddRequest) {
        logger.info("addNewHouse() - start");
        House house = houseMapper.houseAddRequestToHouse(houseAddRequest);
        List<MultipartFile> images = houseAddRequest.getImages();
        logger.info("addNewHouse() - start save and set filename images");
        house.setImage1(fileUtil.saveFile(images.get(0)));
        house.setImage2(fileUtil.saveFile(images.get(1)));
        house.setImage3(fileUtil.saveFile(images.get(2)));
        house.setImage4(fileUtil.saveFile(images.get(3)));
        house.setImage5(fileUtil.saveFile(images.get(4)));
        logger.info("addNewHouse() - set current House object to Floors and Sections");
        house.getFloors().forEach(floor -> floor.setHouse(house));
        house.getSections().forEach(section -> section.setHouse(house));
        House save = houseRepo.save(house);
        logger.info("addNewHouse() - success save new House with id: {}", save.getId());
    }

    @Override
    public Page<HouseShortResponse> getHouses(int page, int pageSize, Map<String, String> searchParams) {
        logger.info("getHouses() -> start with parameters: {}", searchParams);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("name").descending());
        searchParams.remove("page");
        searchParams.remove("pageSize");
        HouseSpecification specification = new HouseSpecification(searchParams);
        Page<House> all = houseRepo.findAll(specification, pageable);
        List<HouseShortResponse> shortResponses = houseMapper.houseListToHouseShortResponseList(all.getContent());
        Page<HouseShortResponse> shortResponsePage = new PageImpl<>(shortResponses, pageable, all.getTotalElements());
        logger.info("getHouses() -> exit, return pages element: {}", shortResponsePage.getNumberOfElements());
        return shortResponsePage;
    }

    @Override
    public boolean deleteHouseById(Long houseId) {
        logger.info("deleteHouseById() - start");
        Optional<House> byId = houseRepo.findById(houseId);
        House house = byId.orElseThrow(() -> {
            logger.error("House with id: {} not found", houseId);
            return new EntityNotFoundException(String.format("ERROR: House with id: %s not found", houseId));
        });
        //TODO add check uses house anymore
        house.setDeleted(true);
        houseRepo.save(house);
        logger.info("deleteHouseById() - exit, house with id: {} marked deleted", houseId);
        return true;
    }

    @Override
    public HouseViewResponse getHouseById(Long houseId) {
        logger.info("getHouseById() -> start, with id: {}", houseId);
        Optional<House> byId = houseRepo.findById(houseId);
        House house = byId.orElseThrow(() -> {
            logger.error("getHouseById() -> House with id: {} not found", houseId);
            return new EntityNotFoundException(String.format("House with id: %s not found", houseId));
        });
        HouseViewResponse response = houseMapper.houseToHouseResponse(house);
        logger.info("getHouseById() -> exit");
        return response;
    }
}
