package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.MainPage;
import com.example.myhouse24admin.entity.MainPageBlock;
import com.example.myhouse24admin.entity.Seo;
import com.example.myhouse24admin.mapper.MainPageMapper;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageBlockRequest;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageRequest;
import com.example.myhouse24admin.model.siteManagement.mainPage.MainPageResponse;
import com.example.myhouse24admin.repository.MainPageBlockRepo;
import com.example.myhouse24admin.repository.MainPageRepo;
import com.example.myhouse24admin.service.MainPageService;
import com.example.myhouse24admin.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class MainPageServiceImpl implements MainPageService {
    private final MainPageRepo mainPageRepo;
    private final MainPageMapper mainPageMapper;
    private final MainPageBlockRepo mainPageBlockRepo;
    private final UploadFileUtil uploadFileUtil;
    private final Logger logger = LogManager.getLogger(MainPageServiceImpl.class);

    public MainPageServiceImpl(MainPageRepo mainPageRepo,
                               MainPageMapper mainPageMapper,
                               MainPageBlockRepo mainPageBlockRepo,
                               UploadFileUtil uploadFileUtil) {
        this.mainPageRepo = mainPageRepo;
        this.mainPageMapper = mainPageMapper;
        this.mainPageBlockRepo = mainPageBlockRepo;
        this.uploadFileUtil = uploadFileUtil;
    }

    @Override
    public void createMainPageIfNotExist() {
        logger.info("createServicesPageIfNotExist - Creating main page if it doesn't exist");
        if(isTableEmpty()){
            MainPage mainPage = mainPageMapper.createMainPage("", new Seo());
            mainPageRepo.save(mainPage);
            logger.info("createServicesPageIfNotExist - Main page was created");
        } else {
            logger.info("createMainPageIfNotExist - Main page has already been created");
        }
    }

    private boolean isTableEmpty() {
        return mainPageRepo.count() == 0;
    }

    @Override
    public MainPageResponse getMainPageResponse() {
        logger.info("getMainPageResponse - Getting main page response");
        MainPage mainPage = mainPageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("Main page was not found by id 1"));
        List<MainPageBlock> mainPageBlocks = mainPageBlockRepo.findAll();
        MainPageResponse mainPageResponse = mainPageMapper.mainPageToMainPageResponse(mainPage, mainPageBlocks);
        logger.info("getMainPageResponse - Main page response was got");
        return mainPageResponse;
    }

    @Override
    public void updateMainPage(MainPageRequest mainPageRequest) {
        logger.info("updateMainPage - Updating main page");
        deleteMainPageBlocks(mainPageRequest.getIdsToDelete());
        updateMainPageBlocks(mainPageRequest.getMainPageBlocks());
        MainPage mainPage = mainPageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("Main page was not found by id 1"));
        String image1Name = updateMainPageImage(mainPageRequest.getImage1(), mainPage.getImage1());
        String image2Name = updateMainPageImage(mainPageRequest.getImage2(), mainPage.getImage2());
        String image3Name = updateMainPageImage(mainPageRequest.getImage3(), mainPage.getImage3());
        mainPageMapper.updateMainPage(mainPage, mainPageRequest, image1Name, image2Name, image3Name);
        mainPageRepo.save(mainPage);
        logger.info("updateMainPage - Main page was updated");
    }

    private String updateMainPageImage(MultipartFile imageFile, String image) {
        if(imageFile.isEmpty()){
            return image;
        } else {
            uploadFileUtil.deleteFile(image);
            return uploadFileUtil.saveMultipartFile(imageFile);
        }
    }

    private void deleteMainPageBlocks(List<Long> idsToDelete) {
        if(idsToDelete != null){
            mainPageRepo.deleteAllById(idsToDelete);
        }
    }
    private void updateMainPageBlocks(List<MainPageBlockRequest> mainPageBlocks) {
        if(mainPageBlocks != null){
            for(MainPageBlockRequest mainPageBlockRequest: mainPageBlocks){
                Optional<MainPageBlock> mainPageBlockOptional = mainPageBlockRepo.findById(mainPageBlockRequest.getId());
                if(mainPageBlockOptional.isEmpty()){
                    createMainPageBlock(mainPageBlockRequest);
                } else {
                    MainPageBlock mainPageBlock = mainPageBlockOptional.get();
                    updateMainPageBlock(mainPageBlockRequest, mainPageBlock);
                }
            }
        }
    }
    private void createMainPageBlock(MainPageBlockRequest mainPageBlockRequest){
        String imageName = uploadFileUtil.saveMultipartFile(mainPageBlockRequest.getImage());
        MainPageBlock mainPageBlock = mainPageMapper.createMainPageBlock(mainPageBlockRequest, imageName);
        mainPageBlockRepo.save(mainPageBlock);
    }
    private void updateMainPageBlock(MainPageBlockRequest mainPageBlockRequest, MainPageBlock mainPageBlock) {
        String imageName = updateMainBlockImage(mainPageBlockRequest.getImage(), mainPageBlock);
        mainPageMapper.updateMainPageBlock(mainPageBlock, mainPageBlockRequest, imageName);
        mainPageBlockRepo.save(mainPageBlock);
    }
    private String updateMainBlockImage(MultipartFile image,
                                        MainPageBlock mainPageBlock){
        if(image.isEmpty()){
            return mainPageBlock.getImage();
        } else {
            uploadFileUtil.deleteFile(mainPageBlock.getImage());
            return uploadFileUtil.saveMultipartFile(image);
        }
    }

}
