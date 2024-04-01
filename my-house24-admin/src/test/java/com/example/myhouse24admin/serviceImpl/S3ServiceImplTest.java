package com.example.myhouse24admin.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @Mock
    private AmazonS3 s3client;
    @InjectMocks
    private S3ServiceImpl s3Service;
    private MockMultipartFile mockMultipartFile;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", "bucketName");
        mockMultipartFile = new MockMultipartFile(
                "keyName",
                "my-file.txt",
                "text/plain", "my-file.txt".getBytes()
        );
    }

    @Test
    void uploadMultipartFile() throws IOException {
        // when
        when(s3client.putObject(any(String.class), any(String.class), any(InputStream.class), eq(null)))
                .thenReturn(new PutObjectResult());

        s3Service.uploadMultipartFile("keyName", mockMultipartFile);

        // then
        verify(s3client).putObject(
                eq("bucketName"),
                eq("keyName"),
                any(InputStream.class),
                eq(null));
    }

    @Test
    void uploadFile() throws IOException {
        // when
        when(s3client.putObject(any(String.class), any(String.class), any(FileInputStream.class), eq(null)))
                .thenReturn(new PutObjectResult());
        s3Service.uploadFile("keyName", File.createTempFile(mockMultipartFile.getName(), ".txt"));

        // then
        verify(s3client).putObject(
                eq("bucketName"),
                eq("keyName"),
                any(FileInputStream.class),
                eq(null)
        );
    }

    @Test
    void uploadInputStream() throws IOException {
        // when
        when(s3client.putObject(any(String.class), any(String.class), any(InputStream.class), eq(null)))
                .thenReturn(new PutObjectResult());
        s3Service.uploadInputStream("keyName", mockMultipartFile.getInputStream());

        // then
        verify(s3client).putObject(
                eq("bucketName"),
                eq("keyName"),
                any(InputStream.class),
                eq(null)
        );
    }

    @Test
    void getS3Object() {
        // when
        when(s3client.getObject(any(String.class), any(String.class)))
                .thenReturn(new S3Object());
        when(s3client.doesObjectExist(any(String.class), any(String.class)))
                .thenReturn(true);
        S3Object keyName = s3Service.getS3Object("keyName");

        // then
        assertNotNull(keyName);
    }

    @Test
    void deleteFile() {
        // when
        when(s3client.doesObjectExist(any(String.class), any(String.class)))
                .thenReturn(false);
        boolean deleted = s3Service.deleteFile("keyName");

        // then
        assertTrue(deleted);
        verify(s3client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void createBucketIfNotExists_WhenBucketNotExists() {
        // when
        when(s3client.doesBucketExistV2(any(String.class)))
                .thenReturn(false);
        s3Service.createBucketIfNotExists();
        // then
        verify(s3client).createBucket(any(CreateBucketRequest.class));
    }

    @Test
    void createBucketIfNotExists_WhenBucketExists() {
        // when
        when(s3client.doesBucketExistV2(any(String.class)))
                .thenReturn(true);
        s3Service.createBucketIfNotExists();
        // then
        verify(s3client, never()).createBucket(any(CreateBucketRequest.class));
    }
}