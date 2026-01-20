package de.hskl.cnseqrcode.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class QrGeneratorTest {

    @Test
    void generate_shouldReturnPngData() {
        byte[] data = QrGenerator.generate("hello");

        assertNotNull(data);
        assertTrue(data.length > 1000);
    }

    @Test
    void generate_invalidInput_shouldThrow() {
        assertThrows(RuntimeException.class, () -> QrGenerator.generate(null));
    }
}
