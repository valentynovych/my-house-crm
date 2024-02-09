package com.example.myhouse24admin.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class UploadFileUtil {
    @Value("${upload.path}")
    private String uploadPath;
    private final Logger logger = LogManager.getLogger(UploadFileUtil.class);

    public void deleteFile(String filename) {
        File deletedImage = new File(uploadPath + "/" + filename);
        if (deletedImage.delete()) {
            logger.info("image" + deletedImage.getAbsolutePath() + "has been delete");
        } else {
            logger.info("image" + deletedImage.getAbsolutePath() + "not delete");
        }
    }

    public String saveFile(MultipartFile file) {
        String fileName = null;
        if (!file.isEmpty()) {
            createDirectoryIfNotExist();
            String uuidFile = UUID.randomUUID().toString();
            try {
                fileName = uuidFile + "_" + file.getOriginalFilename();
                file.transferTo(new File(uploadPath + "/" + fileName));
            } catch (IOException e) {
                logger.error("Error transfer file: {} to directory: {}, because: {}", fileName, uploadPath, e.getCause());
            }
        }
        return fileName;
    }

    private void createDirectoryIfNotExist() {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean mkdir = uploadDir.mkdir();
            if (mkdir) {
                logger.info("create directory for uploads file");
            } else {
                logger.info("directory for uploads file is not create");
            }
        }
    }
}
