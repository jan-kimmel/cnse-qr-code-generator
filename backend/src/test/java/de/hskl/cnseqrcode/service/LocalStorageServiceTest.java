package de.hskl.cnseqrcode.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;

import de.hskl.cnseqrcode.service.StorageServiceImpl.LocalStorageService;

class LocalStorageServiceTest {

    @TempDir
    Path tempDir;

    LocalStorageService service;

    @BeforeEach
    void setup() {
        service = new LocalStorageService(tempDir.toString());
    }

    @Test
    void saveAndLoad_shouldStoreAndReadFile() throws Exception {
        byte[] data = "test".getBytes();

        String path = service.save("abc", data);
        Resource res = service.load("abc");

        assertTrue(Files.exists(Path.of(path)));
        assertEquals("abc.png", Path.of(res.getURI()).getFileName().toString());
    }

    @Test
    void load_missingFile_shouldThrow() {
        assertThrows(RuntimeException.class, () -> service.load("missing"));
    }
}
