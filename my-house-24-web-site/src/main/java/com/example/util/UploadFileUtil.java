package com.example.util;

import com.amazonaws.services.s3.model.S3Object;
import com.example.service.S3Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    public InputStream getFileInputStreamByName(String name){
        S3Object s3Object =  s3Service.getS3Object(name);
        InputStream inputStream = s3Object.getObjectContent();
        return inputStream;
    }

}
