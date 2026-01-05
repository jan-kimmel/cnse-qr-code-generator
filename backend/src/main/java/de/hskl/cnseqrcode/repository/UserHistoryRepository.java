package de.hskl.cnseqrcode.repository;

import java.util.List;

import de.hskl.cnseqrcode.model.UserHistoryEntity;

public interface UserHistoryRepository {
    UserHistoryEntity save(UserHistoryEntity entity);
    List<UserHistoryEntity> findAllByUserId(String userId);
}
