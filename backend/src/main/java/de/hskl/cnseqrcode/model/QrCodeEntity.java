package de.hskl.cnseqrcode.model;

public class QrCodeEntity {
    private String id;
    private String text;
    private String storageUrl;

    public QrCodeEntity() {}
    public QrCodeEntity(String id, String text, String storageUrl) {
        this.id = id;
        this.text = text;
        this.storageUrl = storageUrl;
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
    public String getStorageUrl() {
        return storageUrl;
    }
    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }
}
