package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.House;
import com.example.myhouse24admin.mapper.HouseMapper;
import com.example.myhouse24admin.model.houses.HouseAddRequest;
import com.example.myhouse24admin.repository.HouseRepo;
import com.example.myhouse24admin.service.HouseService;
import com.example.myhouse24admin.util.UploadFileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
}
