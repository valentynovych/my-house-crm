package com.example.myhouse24user.configuration.awsConfiguration;

import com.amazonaws.services.s3.model.S3Object;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import com.example.myhouse24user.service.S3Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

@Component
public class S3ResourceLoader implements ResourceLoader {
    private final  S3Service s3Service;

    public S3ResourceLoader(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Override
    public Resource getResource(String location) {
        S3Object s3Object = s3Service.getS3Object(location);
        return new S3Resource(s3Object);
    }

    @Override
    public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

    private static class S3Resource implements Resource {
        private final S3Object object;

        public S3Resource(S3Object object) {
            this.object = object;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return object.getObjectContent();
        }

        @Override
        public boolean exists() {
            return object.getObjectContent() != null;
        }

        @Override
        public URL getURL() throws IOException {
            return new URL("https", object.getBucketName() + ".s3.us-east-2.amazonaws.com", object.getKey());
        }

        @Override
        public URI getURI() throws IOException {
            return object.getObjectContent().getHttpRequest().getURI();
        }

        @Override
        public File getFile() throws IOException {
            return null;
        }

        @Override
        public long contentLength() throws IOException {
            return object.getObjectMetadata().getContentLength();
        }

        @Override
        public long lastModified() throws IOException {
            return object.getObjectMetadata().getLastModified().getTime();
        }

        @Override
        public Resource createRelative(String relativePath) throws IOException {
            return null;
        }

        @Override
        public String getFilename() {
            return object.getKey();
        }

        @Override
        public String getDescription() {
            return String.format("%s : %s", object.getBucketName(), object.getKey());
        }
    }
}
