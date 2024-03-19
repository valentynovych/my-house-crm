package com.example.myhouse24user.util;

import com.amazonaws.services.s3.model.S3Object;
import com.example.myhouse24user.service.S3Service;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Component
public class UploadFileUtil {
    private final S3Service s3Service;
    private final Logger logger = LogManager.getLogger(UploadFileUtil.class);

    public UploadFileUtil(S3Service s3Service) {
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
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("static/assets/img/avatars/1.png");
        saveFile("defaultAvatar.png",new File(resource.getFile()));
        return "defaultAvatar.png";
    }

    public String saveMultipartFile(MultipartFile file) {
        String fileName = null;
        if (!file.isEmpty()) {
            String uuidFile = UUID.randomUUID().toString();
            try {
                fileName = uuidFile + "_" + file.getOriginalFilename();
                s3Service.uploadMultipartFile(fileName, file);
            } catch (IOException e) {
                logger.error("Error transfer file: {} to AWS bucket, because: {}", fileName, e.getCause());
            }
        }
        return fileName;
    }
    public void saveFile(String fileName, File file){
        try {
            s3Service.uploadFile(fileName, file);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    public InputStream getFileInputStreamByName(String name){
        S3Object s3Object =  s3Service.getS3Object(name);
        InputStream inputStream = s3Object.getObjectContent();
        return inputStream;
    }
}
