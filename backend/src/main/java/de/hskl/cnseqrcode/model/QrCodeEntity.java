package de.hskl.cnseqrcode.model;

import java.time.Instant;

public class QrCodeEntity {
    private String id;
    private String text;
    private Instant createdAt;

    public QrCodeEntity(String id, String text, Instant createdAt) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
    }
    public QrCodeEntity() {
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
