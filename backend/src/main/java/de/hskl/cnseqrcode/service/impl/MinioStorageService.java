package de.hskl.cnseqrcode.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import de.hskl.cnseqrcode.service.StorageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;

@Service
@Profile({"dev", "docker"})
public class MinioStorageService implements StorageService {
    
    private final MinioClient minioClient;
    private final String bucketName;
    
    public MinioStorageService(
        MinioClient minioClient,
        @Value("${app.storage.bucket}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @Override
    public String save(String id, byte[] data) {
        try {
            String filename = id + ".png";
            
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(filename)
                        .stream(bais, data.length, -1)
                        .contentType("image/png")
                        .build()
                );
            }
            
            return String.format("minio://%s/%s", bucketName, filename);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save to MinIO", e);
        }
    }

    @Override
    public Resource load(String id) {
        try {
            String filename = id + ".png";
            
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .build()
            );
            
            InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .build()
            );
            
            return new InputStreamResource(stream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load from MinIO: " + id, e);
        }
    }
}
