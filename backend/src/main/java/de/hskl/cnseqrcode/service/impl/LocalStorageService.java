package de.hskl.cnseqrcode.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import de.hskl.cnseqrcode.service.StorageService;

@Service
public class LocalStorageService implements StorageService {
    private static final Path ROOT = Paths.get("data/qrcodes");

    public LocalStorageService() throws IOException {
        Files.createDirectories(ROOT);
    }

    @Override
    public String save(String id, byte[] data) {
        try {
            Path file = ROOT.resolve(id + ".png");
            Files.write(file, data);
            return file.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store QR code", e);
        }
    }
}
