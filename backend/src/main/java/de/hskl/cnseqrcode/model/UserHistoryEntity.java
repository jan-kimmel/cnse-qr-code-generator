package de.hskl.cnseqrcode.model;

import java.time.Instant;

public class UserHistoryEntity {
    private String userId;
    private String qrCodeId;
    private Instant lastUsedAt;

    public UserHistoryEntity() {}
    public UserHistoryEntity(String userId, String qrCodeId, Instant lastUsedAt) {
        this.userId = userId;
        this.qrCodeId = qrCodeId;
        this.lastUsedAt = lastUsedAt;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getQrCodeId() {
        return qrCodeId;
    }
    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }
    public Instant getLastUsedAt() {
        return lastUsedAt;
    }
    public void setLastUsedAt(Instant lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
}
