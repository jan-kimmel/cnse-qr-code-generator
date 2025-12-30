package de.hskl.cnseqrcode.service;

import org.springframework.core.io.Resource;

public interface StorageService {
    String save(String id, byte[] data);
    Resource load(String id);
}
