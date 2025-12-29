package de.hskl.cnseqrcode.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.hskl.cnseqrcode.model.QrCodeEntity;
import de.hskl.cnseqrcode.repository.QrCodeRepository;

@Service
public class QrCodeService {
    private final QrCodeRepository repository;
    private final StorageService storageService;

    public QrCodeService(QrCodeRepository repository, StorageService storageService) {
        this.repository = repository;
        this.storageService = storageService;
    }

    public QrCodeEntity create(String text) {
        String id = UUID.randomUUID().toString();
        byte[] png = QrGenerator.generate(text);
        storageService.save(id, png);
        QrCodeEntity entity = new QrCodeEntity(id, text, Instant.now());
        return repository.save(entity);
    }
}
