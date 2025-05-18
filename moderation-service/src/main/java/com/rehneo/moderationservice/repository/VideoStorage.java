package com.rehneo.moderationservice.repository;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class VideoStorage {
    @Value("${minio.bucket-name}")
    private String bucketName;
    public static final String STORAGE_FILE_EXTENSION = ".mp4";
    public static final String STORAGE_FILE_PREFIX = "video-";

    private final MinioClient minioClient;

    public byte[] find(int id) throws Exception{
        InputStream obj = minioClient.getObject(GetObjectArgs.builder()
                .object(generateFileName(id))
                .bucket(bucketName)
                .build());
        return obj.readAllBytes();
    }

    public void save(int id, InputStream stream, Long size) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(generateFileName(id))
                        .contentType("video/mp4")
                        .stream(stream, size, -1)
                        .build()
        );
    }

    public void remove(int id) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(generateFileName(id))
                        .build()
        );
    }

    @PostConstruct
    private void initBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateFileName(int id) {
        return STORAGE_FILE_PREFIX + id + STORAGE_FILE_EXTENSION;
    }
}