package de.hskl.cnseqrcode.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import de.hskl.cnseqrcode.api.dto.QrCodeResponseDto;
import de.hskl.cnseqrcode.api.dto.UserHistoryDto;
import de.hskl.cnseqrcode.model.QrCodeEntity;
import de.hskl.cnseqrcode.model.UserHistoryEntity;
import de.hskl.cnseqrcode.repository.QrCodeRepository;
import de.hskl.cnseqrcode.repository.UserHistoryRepository;

@ExtendWith(MockitoExtension.class)
class QrCodeServiceTest {

    @Mock
    QrCodeRepository qrCodeRepository;

    @Mock
    UserHistoryRepository userHistoryRepository;

    @Mock
    StorageService storageService;

    @InjectMocks
    QrCodeService service;

    @Test
    void generate_shouldCreateNewQrCodeAndStoreHistory() {
        String text = "hello";
        String userId = "user1";
        String hash = DigestUtils.sha256Hex(text);

        when(qrCodeRepository.findById(hash)).thenReturn(Optional.empty());
        when(storageService.save(eq(hash), any())).thenReturn("/tmp/" + hash + ".png");

        QrCodeResponseDto result = service.generate(text, userId);

        assertEquals(hash, result.id());
        assertEquals(text, result.text());
        assertTrue(result.imageUrl().contains(hash));

        verify(qrCodeRepository).save(any());
        verify(userHistoryRepository).save(any());
    }

    @Test
    void generate_shouldReuseExistingQrCode() {
        String text = "hello";
        String hash = DigestUtils.sha256Hex(text);

        QrCodeEntity existing = new QrCodeEntity(hash, text, "/stored.png");
        when(qrCodeRepository.findById(hash)).thenReturn(Optional.of(existing));

        QrCodeResponseDto result = service.generate(text, null);

        assertEquals(hash, result.id());
        verify(qrCodeRepository, never()).save(any());
        verify(userHistoryRepository, never()).save(any());
    }

    @Test
    void loadQrCodeImage_shouldDelegateToStorage() {
        Resource resource = mock(Resource.class);
        when(storageService.load("abc")).thenReturn(resource);

        Resource result = service.loadQrCodeImage("abc");

        assertSame(resource, result);
    }

    @Test
    void getUserHistory_shouldReturnTextsInOrder() {
        UserHistoryEntity h1 = new UserHistoryEntity("u", "id1", Instant.now());
        UserHistoryEntity h2 = new UserHistoryEntity("u", "id2", Instant.now().minusSeconds(10));

        when(userHistoryRepository.findAllByUserId("u")).thenReturn(List.of(h1, h2));
        when(qrCodeRepository.findById("id1")).thenReturn(Optional.of(new QrCodeEntity("id1", "A", "x")));
        when(qrCodeRepository.findById("id2")).thenReturn(Optional.of(new QrCodeEntity("id2", "B", "y")));

        UserHistoryDto dto = service.getUserHistory("u");

        assertEquals(List.of("A", "B"), dto.texts());
    }
}
