package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.mapper.AboutPageMapper;
import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageRequest;
import com.example.myhouse24admin.model.siteManagement.aboutPage.AboutPageResponse;
import com.example.myhouse24admin.repository.AboutPageRepo;
import com.example.myhouse24admin.repository.AdditionalGalleryRepo;
import com.example.myhouse24admin.repository.DocumentRepo;
import com.example.myhouse24admin.repository.GalleryRepo;
import com.example.myhouse24admin.service.AboutPageService;
import com.example.myhouse24admin.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AboutPageServiceImpl implements AboutPageService {
    private final AboutPageRepo aboutPageRepo;
    private final AboutPageMapper aboutPageMapper;
    private final GalleryRepo galleryRepo;
    private final AdditionalGalleryRepo additionalGalleryRepo;
    private final DocumentRepo documentRepo;
    private final UploadFileUtil uploadFileUtil;
    private final Logger logger = LogManager.getLogger(AboutPageServiceImpl.class);

    public AboutPageServiceImpl(AboutPageRepo aboutPageRepo,
                                AboutPageMapper aboutPageMapper,
                                GalleryRepo galleryRepo,
                                AdditionalGalleryRepo additionalGalleryRepo,
                                DocumentRepo documentRepo,
                                UploadFileUtil uploadFileUtil) {
        this.aboutPageRepo = aboutPageRepo;
        this.aboutPageMapper = aboutPageMapper;
        this.galleryRepo = galleryRepo;
        this.additionalGalleryRepo = additionalGalleryRepo;
        this.documentRepo = documentRepo;
        this.uploadFileUtil = uploadFileUtil;
    }

    @Override
    public void createAboutPageIfNotExist() {
        logger.info("createAboutPageIfNotExist - Creating about page if it doesn't exist");
        if(isTableEmpty()){
            AboutPage aboutPage = aboutPageMapper.createAboutPage("",new Seo());
            aboutPageRepo.save(aboutPage);
            logger.info("createAboutPageIfNotExist - About page was created");
        } else {
            logger.info("createAboutPageIfNotExist - About page has already been created");
        }

    }

    private boolean isTableEmpty() {
        return aboutPageRepo.count() == 0;
    }

    @Override
    public AboutPageResponse getAboutPageResponse() {
        logger.info("getAboutPageResponse - Getting about page response");
        AboutPage aboutPage = aboutPageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("About page was not found by id 1"));
        List<Gallery> gallery = galleryRepo.findAll();
        List<AdditionalGallery> additionalGallery = additionalGalleryRepo.findAll();
        List<Document> documents = documentRepo.findAll();
        AboutPageResponse aboutPageResponse = aboutPageMapper.aboutPageToAboutPageResponse(aboutPage, gallery, additionalGallery, documents);
        logger.info("getAboutPageResponse - About page response was got");
        return aboutPageResponse;
    }

    @Override
    public void updateAboutPage(AboutPageRequest aboutPageRequest) {
        logger.info("updateAboutPage - Updating about page");
        deleteGalleryImages(aboutPageRequest.getGalleryIdsToDelete());
        deleteAdditionalGalleryImages(aboutPageRequest.getAdditionalGalleryIdsToDelete());
        deleteDocuments(aboutPageRequest.getDocumentIdsToDelete());
        saveDocuments(aboutPageRequest.getNewDocuments());
        saveGalleryImages(aboutPageRequest.getNewImages());
        saveAdditionalGalleryImages(aboutPageRequest.getAdditionalNewImages());
        AboutPage aboutPage = aboutPageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("About page was not found by id 1"));
        String imageName = updateImage(aboutPageRequest.getDirectorImage(), aboutPage);
        aboutPageMapper.updateAboutPage(aboutPage, aboutPageRequest, imageName);
        aboutPageRepo.save(aboutPage);
        logger.info("updateAboutPage - About page was updated");
    }

    private void deleteDocuments(List<Long> documentIdsToDelete) {
        if(documentIdsToDelete != null){
            List<Document> documents = documentRepo.findAllById(documentIdsToDelete);
            for (Document document: documents){
                uploadFileUtil.deleteFile(document.getName());
            }
            documentRepo.deleteAllById(documentIdsToDelete);
        }
    }

    private void deleteAdditionalGalleryImages(List<Long> additionalGalleryIdsToDelete) {
        if(additionalGalleryIdsToDelete != null) {
            List<AdditionalGallery> additionalGallery = additionalGalleryRepo.findAllById(additionalGalleryIdsToDelete);
            for (AdditionalGallery additionalImage : additionalGallery) {
                uploadFileUtil.deleteFile(additionalImage.getImage());
            }
            additionalGalleryRepo.deleteAllById(additionalGalleryIdsToDelete);
        }
    }

    private void deleteGalleryImages(List<Long> galleryIdsToDelete) {
        if(galleryIdsToDelete != null) {
            List<Gallery> gallery = galleryRepo.findAllById(galleryIdsToDelete);
            for (Gallery image : gallery) {
                uploadFileUtil.deleteFile(image.getImage());
            }
            galleryRepo.deleteAllById(galleryIdsToDelete);
        }
    }
    private void saveDocuments(List<MultipartFile> newDocuments) {
        if(newDocuments != null){
            for(MultipartFile newDocument: newDocuments){
                String documentName = uploadFileUtil.saveFile(newDocument);
                Document document = new Document();
                document.setName(documentName);
                documentRepo.save(document);
            }
        }
    }

    private void saveGalleryImages(List<MultipartFile> newImages) {
        if(newImages != null) {
            for (MultipartFile newImage : newImages) {
                String imageName = uploadFileUtil.saveFile(newImage);
                Gallery gallery = new Gallery();
                gallery.setImage(imageName);
                galleryRepo.save(gallery);
            }
        }
    }
    private void saveAdditionalGalleryImages(List<MultipartFile> additionalNewImages) {
        if(additionalNewImages != null) {
            for (MultipartFile additionalNewImage : additionalNewImages) {
                String imageName = uploadFileUtil.saveFile(additionalNewImage);
                AdditionalGallery additionalGallery = new AdditionalGallery();
                additionalGallery.setImage(imageName);
                additionalGalleryRepo.save(additionalGallery);
            }
        }
    }
    private String updateImage(MultipartFile image,
                               AboutPage aboutPage){
        if(image.isEmpty()){
            return aboutPage.getDirectorImage();
        } else {
            uploadFileUtil.deleteFile(aboutPage.getDirectorImage());
            return uploadFileUtil.saveFile(image);
        }
    }
}
