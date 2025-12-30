package de.hskl.cnseqrcode.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import de.hskl.cnseqrcode.service.StorageService;

@Service
public class LocalStorageService implements StorageService {
    private final Path storageDir;

    public LocalStorageService(@Value("${qr.storage.path}") String storagePath) throws IOException {
        this.storageDir = Path.of(storagePath);
        try {
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory: " + storagePath, e);
        }
    }

    @Override
    public String save(String id, byte[] data) {
        try {
            Path file = storageDir.resolve(id + ".png");
            Files.write(file, data);
            return file.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store QR code", e);
        }
    }

    @Override
    public Resource load(String id) {
        try {
            Path file = storageDir.resolve(id + ".png");
            if (!Files.exists(file)) {
                throw new FileNotFoundException(id);
            }
            return new UrlResource(file.toUri());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load QR Code " + id, e);
        }
    }
}
