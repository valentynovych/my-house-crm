package com.example.myhouse24admin.util;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.myhouse24admin.service.S3Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class UploadFileUtil {
//    private final ResourceLoader resourceLoader;
    private final S3Service s3Service;
    private final Logger logger = LogManager.getLogger(UploadFileUtil.class);

    public UploadFileUtil(S3Service s3Service) {
//        this.resourceLoader = resourceLoader;
        this.s3Service = s3Service;
    }

    public void deleteFile(String filename) {
        if (s3Service.deleteFile(filename)) {
            logger.info("image: {} - has been delete from bucked", filename);
        } else {
            logger.info("image: {} - not delete from bucked", filename);
        }
    }

    public String saveDefaultOwnerImage() {
//        Resource resource = resourceLoader.getResource("classpath:static/assets/img/avatars/1.png");
//        try {
//            s3Service.uploadFile("defaultAvatar.png", (MultipartFile) resource.getFile());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return "defaultAvatar.png";
    }

    public String saveFile(MultipartFile file) {
        String fileName = null;
        if (!file.isEmpty()) {
            String uuidFile = UUID.randomUUID().toString();
            try {
                fileName = uuidFile + "_" + file.getOriginalFilename();
                s3Service.uploadFile(fileName, file);
            } catch (IOException e) {
                logger.error("Error transfer file: {} to AWS bucket, because: {}", fileName, e.getCause());
            }
        }
        return fileName;
    }
    public File getFileByName(String name){
        S3Object s3Object =  s3Service.getS3Object(name);
        S3ObjectInputStream s3is = s3Object.getObjectContent();
        return null;
    }

}
