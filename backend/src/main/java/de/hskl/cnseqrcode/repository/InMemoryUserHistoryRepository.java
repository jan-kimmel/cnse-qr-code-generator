package de.hskl.cnseqrcode.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import de.hskl.cnseqrcode.model.UserHistoryEntity;

@Repository
@Profile("local")
public class InMemoryUserHistoryRepository implements UserHistoryRepository {
    private final Map<String, List<UserHistoryEntity>> storage = new ConcurrentHashMap<>();

    @Override
    public UserHistoryEntity save(UserHistoryEntity entity) {
        storage.computeIfAbsent(entity.getUserId(), k -> new ArrayList<>());
        
        storage.get(entity.getUserId()).removeIf(e -> 
            e.getQrCodeId().equals(entity.getQrCodeId())
        );
        
        storage.get(entity.getUserId()).add(entity);
        
        return entity;
    }

    @Override
    public List<UserHistoryEntity> findAllByUserId(String userId) {
        return storage.getOrDefault(userId, List.of()).stream()
            .sorted(Comparator.comparing(UserHistoryEntity::getLastUsedAt).reversed())
            .toList();
    }
}
