package de.hskl.cnseqrcode.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import de.hskl.cnseqrcode.service.StorageService;

@Service
@Profile("prod")
public class GcsStorageService implements StorageService {
    
    private final Storage storage;
    private final String bucketName;
    
    public GcsStorageService(
        Storage storage,
        @Value("${qr.storage.bucket}") String bucketName) {
        this.storage = storage;
        this.bucketName = bucketName;
    }

    @Override
    public String save(String id, byte[] data) {
        try {
            String filename = id + ".png";
            BlobId blobId = BlobId.of(bucketName, filename);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("image/png")
                .setCacheControl("public, max-age=31536000")
                .build();
            
            storage.create(blobInfo, data);
            
            return String.format("gs://%s/%s", bucketName, filename);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save to GCS", e);
        }
    }

    @Override
    public Resource load(String id) {
        try {
            String filename = id + ".png";
            Blob blob = storage.get(BlobId.of(bucketName, filename));
            
            if (blob == null || !blob.exists()) {
                throw new RuntimeException("QR code not found: " + id);
            }
            
            byte[] content = blob.getContent();
            return new ByteArrayResource(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load from GCS: " + id, e);
        }
    }
}
