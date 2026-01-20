package de.hskl.cnseqrcode.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import de.hskl.cnseqrcode.api.dto.QrCodeResponseDto;
import de.hskl.cnseqrcode.api.dto.UserHistoryDto;
import de.hskl.cnseqrcode.model.QrCodeEntity;
import de.hskl.cnseqrcode.model.UserHistoryEntity;
import de.hskl.cnseqrcode.repository.QrCodeRepository;
import de.hskl.cnseqrcode.repository.UserHistoryRepository;

@Service
public class QrCodeService {
    private final QrCodeRepository qrCodeRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final StorageService storageService;

    public QrCodeService(QrCodeRepository qrCodeRepository, UserHistoryRepository userHistoryRepository, StorageService storageService) {
        this.qrCodeRepository = qrCodeRepository;
        this.userHistoryRepository = userHistoryRepository;
        this.storageService = storageService;
    }

    public QrCodeResponseDto generate(String text, String userId) {
        try {
            String hash = DigestUtils.sha256Hex(text);
            Optional<QrCodeEntity> existing = qrCodeRepository.findById(hash);
            QrCodeEntity qrCode;

            if (existing.isEmpty()) {
                byte[] pngData = QrGenerator.generate(text);
                String storageUrl = storageService.save(hash, pngData);
                
                qrCode = new QrCodeEntity(hash, text, storageUrl);
                qrCodeRepository.save(qrCode);
            } else {
                qrCode = existing.get();
            }
            
            if (userId != null && !userId.isBlank()) {
                UserHistoryEntity history = new UserHistoryEntity(
                    userId,
                    hash,
                    Instant.now()
                );
                userHistoryRepository.save(history);
            }
            
            return new QrCodeResponseDto(
                qrCode.getId(),
                qrCode.getText(),
                "/api/qr-codes/" + qrCode.getId() + "/image"
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    public Resource loadQrCodeImage(String id) {
        return storageService.load(id);
    }
    
    public UserHistoryDto getUserHistory(String userId) {
        List<String> texts = userHistoryRepository.findAllByUserId(userId).stream()
            .map(history -> qrCodeRepository.findById(history.getQrCodeId())
                .map(QrCodeEntity::getText)
                .orElseThrow(() -> new RuntimeException("QR code not found: " + history.getQrCodeId()))
            ).toList();
        return new UserHistoryDto(texts);
    }
}
