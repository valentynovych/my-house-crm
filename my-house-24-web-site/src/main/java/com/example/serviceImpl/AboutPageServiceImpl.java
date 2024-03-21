package com.example.serviceImpl;

import com.example.entity.AboutPage;
import com.example.entity.AdditionalGallery;
import com.example.entity.Document;
import com.example.entity.Gallery;
import com.example.mapper.AboutPageMapper;
import com.example.model.aboutPage.AboutPageResponse;
import com.example.repository.AboutPageRepo;
import com.example.repository.AdditionalGalleryRepo;
import com.example.repository.DocumentRepo;
import com.example.repository.GalleryRepo;
import com.example.service.AboutPageService;
import com.example.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class AboutPageServiceImpl implements AboutPageService {
    private final AboutPageRepo aboutPageRepo;
    private final GalleryRepo galleryRepo;
    private final AdditionalGalleryRepo additionalGalleryRepo;
    private final DocumentRepo documentRepo;
    private final AboutPageMapper aboutPageMapper;
    private final UploadFileUtil uploadFileUtil;
    private final Logger logger = LogManager.getLogger(AboutPageServiceImpl.class);

    public AboutPageServiceImpl(AboutPageRepo aboutPageRepo, GalleryRepo galleryRepo,
                                AdditionalGalleryRepo additionalGalleryRepo,
                                DocumentRepo documentRepo, AboutPageMapper aboutPageMapper,
                                UploadFileUtil uploadFileUtil) {
        this.aboutPageRepo = aboutPageRepo;
        this.galleryRepo = galleryRepo;
        this.additionalGalleryRepo = additionalGalleryRepo;
        this.documentRepo = documentRepo;
        this.aboutPageMapper = aboutPageMapper;
        this.uploadFileUtil = uploadFileUtil;
    }

    @Override
    public AboutPageResponse getAboutPageResponse() {
        logger.info("getAboutPageResponse() - Getting about page response");
        AboutPage aboutPage = aboutPageRepo.findById(1L).orElseThrow(()-> new EntityNotFoundException("About page was not found by id 1"));
        List<Gallery> gallery = galleryRepo.findAll();
        List<AdditionalGallery> additionalGallery = additionalGalleryRepo.findAll();
        List<Document> documents = documentRepo.findAll();
        AboutPageResponse aboutPageResponse = aboutPageMapper.aboutPageToAboutPageResponse(aboutPage, gallery, additionalGallery, documents);
        logger.info("getAboutPageResponse() - About page response was got");
        return aboutPageResponse;
    }

    @Override
    public byte[] getDocument(String documentName) {
        logger.info("getDocument() - Getting document by name "+documentName);
        InputStream inputStream = uploadFileUtil.getFileInputStreamByName(documentName);
        byte[] file = new byte[0];
        try {
            file = inputStream.readAllBytes();
            logger.info("getDocument() - Document was got");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return file;
    }
}
