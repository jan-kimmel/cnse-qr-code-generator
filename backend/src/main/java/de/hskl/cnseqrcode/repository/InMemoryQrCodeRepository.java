package de.hskl.cnseqrcode.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import de.hskl.cnseqrcode.model.QrCodeEntity;

@Repository
@Profile("local")
public class InMemoryQrCodeRepository implements QrCodeRepository {
    private final Map<String, QrCodeEntity> storage = new ConcurrentHashMap<>();

    @Override
    public QrCodeEntity save(QrCodeEntity entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<QrCodeEntity> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }
}
