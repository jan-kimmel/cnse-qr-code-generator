package de.hskl.cnseqrcode.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import de.hskl.cnseqrcode.model.QrCodeEntity;

@Repository
public class InMemoryQrCodeRepository implements QrCodeRepository {
    private final Map<String, QrCodeEntity> store = new ConcurrentHashMap<>();

    @Override
    public QrCodeEntity save(QrCodeEntity entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<QrCodeEntity> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
