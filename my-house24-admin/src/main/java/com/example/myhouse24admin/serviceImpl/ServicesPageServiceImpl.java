package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.Seo;
import com.example.myhouse24admin.entity.ServicePageBlock;
import com.example.myhouse24admin.entity.ServicesPage;
import com.example.myhouse24admin.mapper.ServicesPageMapper;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicePageBlockRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicePageRequest;
import com.example.myhouse24admin.model.siteManagement.servicesPage.ServicesPageResponse;
import com.example.myhouse24admin.repository.ServicePageBlockRepo;
import com.example.myhouse24admin.repository.ServicesPageRepo;
import com.example.myhouse24admin.service.ServicesPageService;
import com.example.myhouse24admin.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ServicesPageServiceImpl implements ServicesPageService {
    private final ServicesPageRepo servicesPageRepo;
    private final ServicesPageMapper servicesPageMapper;
    private final ServicePageBlockRepo servicePageBlockRepo;
    private final UploadFileUtil uploadFileUtil;
    private final Logger logger = LogManager.getLogger(ServicesPageServiceImpl.class);

    public ServicesPageServiceImpl(ServicesPageRepo servicesPageRepo,
                                   ServicesPageMapper servicesPageMapper,
                                   ServicePageBlockRepo servicePageBlockRepo,
                                   UploadFileUtil uploadFileUtil) {
        this.servicesPageRepo = servicesPageRepo;
        this.servicesPageMapper = servicesPageMapper;
        this.servicePageBlockRepo = servicePageBlockRepo;
        this.uploadFileUtil = uploadFileUtil;
    }

    @Override
    public void createServicesPageIfNotExist() {
        logger.info("createServicesPageIfNotExist - Creating services page if it doesn't exist");
        if(isTableEmpty()) {
            ServicesPage servicesPage = servicesPageMapper.createServicesPage(new Seo());
            ServicePageBlock servicePageBlock = servicesPageMapper.createFirstServicePageBlock("");
            servicesPageRepo.save(servicesPage);
            servicePageBlockRepo.save(servicePageBlock);
            logger.info("createServicesPageIfNotExist - Services page was created");
        } else {
            logger.info("createServicesPageIfNotExist - Services page has already been created");
        }
    }
    private boolean isTableEmpty(){
        return servicesPageRepo.count() == 0;
    }

    @Override
    public ServicesPageResponse getServicesPageResponse() {
        logger.info("getServicesPageResponse - Getting services page response");
        ServicesPage servicesPage = servicesPageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("Service page was not found"));
        List<ServicePageBlock> servicePageBlocks = servicePageBlockRepo.findAll(Sort.by("id"));
        ServicesPageResponse servicesPageResponse = servicesPageMapper.servicesPageToServicesPageResponse(servicesPage, servicePageBlocks);
        logger.info("getServicesPageResponse - Services page response was got");
        return servicesPageResponse;
    }

    @Override
    public void updateServicesPage(ServicePageRequest servicePageRequest) {
        logger.info("updateServicesPage - Updating services page");
        deleteServicePageBlocks(servicePageRequest.getIdsToDelete());
        updateOrCreateServicePageBlocks(servicePageRequest.getServicePageBlocks());
        ServicesPage servicesPage = servicesPageRepo.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Services page was not found by id 1"));
        servicesPageMapper.updateServicesPage(servicesPage, servicePageRequest.getSeoRequest());
        servicesPageRepo.save(servicesPage);
        logger.info("getServicesPageResponse - Services page was updated");
    }

    private void deleteServicePageBlocks(List<Long> idsToDelete) {
        if(idsToDelete != null) {
            servicePageBlockRepo.deleteAllById(idsToDelete);
        }
    }
    private void updateOrCreateServicePageBlocks(List<ServicePageBlockRequest> servicePageBlocks){
        for(ServicePageBlockRequest servicePageBlockRequest : servicePageBlocks){
            Optional<ServicePageBlock> optionalServicePageBlock = servicePageBlockRepo.findById(servicePageBlockRequest.getId());
            if(optionalServicePageBlock.isEmpty()){
                createServicePageBlock(servicePageBlockRequest);
            } else {
                ServicePageBlock servicePageBlock = optionalServicePageBlock.get();
               updateServicePageBlock(servicePageBlockRequest, servicePageBlock);
            }
        }
    }
    private void createServicePageBlock(ServicePageBlockRequest servicePageBlockRequest){
        String imageName = uploadFileUtil.saveMultipartFile(servicePageBlockRequest.getImage());
        ServicePageBlock servicePageBlock = servicesPageMapper.createServicePageBlock(servicePageBlockRequest,imageName);
        servicePageBlockRepo.save(servicePageBlock);
    }
    private void updateServicePageBlock(ServicePageBlockRequest servicePageBlockRequest, ServicePageBlock servicePageBlock){
        String imageName = updateImage(servicePageBlockRequest.getImage(), servicePageBlock);
        servicesPageMapper.updateServicePageBlock(servicePageBlock, servicePageBlockRequest, imageName);
        servicePageBlockRepo.save(servicePageBlock);
    }
    private String updateImage(MultipartFile image,
                       ServicePageBlock servicePageBlockInDb){
        if(image.isEmpty()){
            return servicePageBlockInDb.getImage();
        } else {
            uploadFileUtil.deleteFile(servicePageBlockInDb.getImage());
            return uploadFileUtil.saveMultipartFile(image);
        }
    }

}
