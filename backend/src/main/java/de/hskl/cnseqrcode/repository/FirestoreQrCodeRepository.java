package de.hskl.cnseqrcode.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import de.hskl.cnseqrcode.model.QrCodeEntity;

@Repository
@Profile({"dev", "prod", "docker"})
public class FirestoreQrCodeRepository implements QrCodeRepository {
    
    private final Firestore firestore;
    private static final String COLLECTION = "qr_codes";
    
    public FirestoreQrCodeRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @SuppressWarnings("null")
    @Override
    public QrCodeEntity save(QrCodeEntity entity) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("id", entity.getId());
            data.put("text", entity.getText());
            data.put("storageUrl", entity.getStorageUrl());
            
            firestore.collection(COLLECTION)
                .document(entity.getId())
                .set(data)
                .get();
            
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save QR code to Firestore", e);
        }
    }

    @Override
    public Optional<QrCodeEntity> findById(String id) {
        try {
            @SuppressWarnings("null")
            DocumentSnapshot doc = firestore.collection(COLLECTION)
                .document(id).get().get();
            
            if (!doc.exists()) {
                return Optional.empty();
            }
            
            QrCodeEntity entity = new QrCodeEntity();
            entity.setId(doc.getString("id"));
            entity.setText(doc.getString("text"));
            entity.setStorageUrl(doc.getString("storageUrl"));
            
            return Optional.of(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read QR code from Firestore", e);
        }
    }
}
