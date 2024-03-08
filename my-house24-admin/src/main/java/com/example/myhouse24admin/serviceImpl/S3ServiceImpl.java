package com.example.myhouse24admin.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.example.myhouse24admin.service.S3Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3ServiceImpl implements S3Service {

    @Value("${aws.bucketName}")
    private String bucketName;
    private final AmazonS3 s3client;
    private final Logger logger = LogManager.getLogger(S3ServiceImpl.class);

    public S3ServiceImpl(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    @Override
    public void uploadFile(String keyName, MultipartFile file) throws IOException {
        logger.info("uploadFile() -> start uploading file to AWS bucket {file: {}}", file);
        var putObjectResult = s3client.putObject(bucketName, keyName, file.getInputStream(), null);
        logger.info("uploadFile() -> end, success upload file to AWS, putObjectMetadata: {}", putObjectResult.getMetadata());
    }

    @Override
    public S3Object getFile(String keyName) {
        logger.info("getFile() -> start getting file from AWS, keyName: {}", keyName);
        S3Object object = s3client.getObject(bucketName, keyName);
        logger.info("getFile() -> end, success get file: {}", object.getKey());
        return object;
    }

    @Override
    public boolean deleteFile(String keyName) {
        logger.info("deleteFile() -> start deleting file from AWS, keyName: {}", keyName);
        s3client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
        logger.info("deleteFile() -> end, success delete file, keyName: {}", keyName);
        return !s3client.doesObjectExist(bucketName, keyName);
    }

    @Override
    public void createBucketIfNotExists() {
        logger.info("createBucketIfNotExists() -> start");
        boolean isExists = s3client.doesBucketExistV2(bucketName);
        if (!isExists) {
            logger.info("createBucketIfNotExists() -> Bucket with name: {} " +
                    "not exist in AWS Cloud, create new bucket", bucketName);
            s3client.createBucket(new CreateBucketRequest(bucketName));
            logger.info("createBucketIfNotExists() -> Bucket with name: {} have been create", bucketName);
        } else {
            logger.info("Bucket with name: {} already is exists", bucketName);
        }
        logger.info("createBucketIfNotExists() -> end");
    }
}