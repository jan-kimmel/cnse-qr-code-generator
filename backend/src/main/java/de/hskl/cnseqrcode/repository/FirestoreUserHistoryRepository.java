package de.hskl.cnseqrcode.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;

import de.hskl.cnseqrcode.model.UserHistoryEntity;

@Repository
@Profile({"dev", "prod", "docker"})
public class FirestoreUserHistoryRepository implements UserHistoryRepository {
    
    private final Firestore firestore;
    private static final String COLLECTION = "user_history";
    
    public FirestoreUserHistoryRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @SuppressWarnings("null")
    @Override
    public UserHistoryEntity save(UserHistoryEntity entity) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("qrCodeId", entity.getQrCodeId());
            data.put("lastUsedAt", Timestamp.ofTimeSecondsAndNanos(
                entity.getLastUsedAt().getEpochSecond(),
                entity.getLastUsedAt().getNano()
            ));
            
            firestore.collection(COLLECTION)
                .document(entity.getUserId())
                .collection("history")
                .document(entity.getQrCodeId())
                .set(data, SetOptions.merge())
                .get();
            
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user history to Firestore", e);
        }
    }

    @Override
    public List<UserHistoryEntity> findAllByUserId(String userId) {
        try {
            @SuppressWarnings("null")
            QuerySnapshot snapshot = firestore.collection(COLLECTION)
                .document(userId)
                .collection("history")
                .orderBy("lastUsedAt", Query.Direction.DESCENDING)
                .get().get();
            
            List<UserHistoryEntity> result = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshot) {
                UserHistoryEntity entity = new UserHistoryEntity();
                entity.setUserId(userId);
                entity.setQrCodeId(doc.getString("qrCodeId"));
                
                Timestamp timestamp = doc.getTimestamp("lastUsedAt");
                if (timestamp != null) {
                    entity.setLastUsedAt(Instant.ofEpochSecond(
                        timestamp.getSeconds(),
                        timestamp.getNanos()
                    ));
                }
                
                result.add(entity);
            }
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read user history from Firestore", e);
        }
    }
}
