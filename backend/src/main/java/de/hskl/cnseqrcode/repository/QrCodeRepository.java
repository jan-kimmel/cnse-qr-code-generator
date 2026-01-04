package de.hskl.cnseqrcode.repository;

import java.util.List;
import java.util.Optional;

import de.hskl.cnseqrcode.model.QrCodeEntity;

public interface QrCodeRepository {
    QrCodeEntity save(QrCodeEntity entity);
    Optional<QrCodeEntity> findById(String id);
    List<QrCodeEntity> findAllByUserId(String userId);
}
