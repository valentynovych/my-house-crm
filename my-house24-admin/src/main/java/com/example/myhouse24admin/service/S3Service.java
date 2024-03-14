package com.example.myhouse24admin.service;

import com.amazonaws.services.s3.model.S3Object;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface S3Service {
    void uploadMultipartFile(String keyName, MultipartFile file) throws IOException;
    void uploadFile(String keyName, File file) throws IOException;
    void uploadInputStream(String keyName, InputStream inputStream);

    S3Object getS3Object(String keyName);

    boolean deleteFile(String keyName);

    void createBucketIfNotExists();
}
