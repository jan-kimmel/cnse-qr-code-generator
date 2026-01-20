package de.hskl.cnseqrcode.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.hskl.cnseqrcode.model.QrCodeEntity;

public class InMemoryQrCodeRepositoryTest {

    InMemoryQrCodeRepository repo = new InMemoryQrCodeRepository();

    @Test
    void saveAndFind_shouldWork() {
        QrCodeEntity entity = new QrCodeEntity("id", "text", "url");

        repo.save(entity);
        Optional<QrCodeEntity> result = repo.findById("id");

        assertTrue(result.isPresent());
        assertEquals("text", result.get().getText());
    }

    @Test
    void find_missing_shouldReturnEmpty() {
        assertTrue(repo.findById("x").isEmpty());
    }
}
