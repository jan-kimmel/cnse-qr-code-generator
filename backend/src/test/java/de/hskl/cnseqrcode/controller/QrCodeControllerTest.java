package de.hskl.cnseqrcode.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import de.hskl.cnseqrcode.api.controller.QrCodeController;
import de.hskl.cnseqrcode.api.dto.QrCodeRequestDto;
import de.hskl.cnseqrcode.api.dto.QrCodeResponseDto;
import de.hskl.cnseqrcode.api.dto.UserHistoryDto;
import de.hskl.cnseqrcode.service.QrCodeService;
import jakarta.servlet.http.HttpServletRequest;

class QrCodeControllerTest {

    @Mock
    private QrCodeService qrCodeService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private QrCodeController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateQrCode() {
        QrCodeRequestDto dto = new QrCodeRequestDto("Hello");
        when(request.getAttribute("userId")).thenReturn("user123");
        when(qrCodeService.generate("Hello", "user123"))
            .thenReturn(new QrCodeResponseDto("hash1", "Hello", "/api/qr-codes/hash1/image"));

        ResponseEntity<QrCodeResponseDto> response = controller.createQrCode(dto, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("hash1", response.getBody().id());
        verify(qrCodeService).generate("Hello", "user123");
    }

    @Test
    void testGetQrCode() throws IOException {
        Resource resource = new ByteArrayResource(new byte[]{1,2,3});
        when(qrCodeService.loadQrCodeImage("hash1")).thenReturn(resource);

        ResponseEntity<Resource> response = controller.getQrCode("hash1");

        assertEquals(200, response.getStatusCode().value());
        assertArrayEquals(new byte[]{1,2,3}, response.getBody().getInputStream().readAllBytes());
        verify(qrCodeService).loadQrCodeImage("hash1");
    }

    @Test
    void testDownloadQrCode() throws IOException {
        Resource resource = new ByteArrayResource(new byte[]{4,5,6});
        when(qrCodeService.loadQrCodeImage("hash2")).thenReturn(resource);

        ResponseEntity<Resource> response = controller.downloadQrCode("hash2");

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("attachment"));
        assertArrayEquals(new byte[]{4,5,6}, response.getBody().getInputStream().readAllBytes());
        verify(qrCodeService).loadQrCodeImage("hash2");
    }

    @Test
    void testGetHistoryWithUserId() {
        when(request.getAttribute("userId")).thenReturn("user1");
        when(qrCodeService.getUserHistory("user1"))
            .thenReturn(new UserHistoryDto(java.util.List.of("Text1", "Text2")));

        ResponseEntity<UserHistoryDto> response = controller.getHistory(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().texts().size());
        verify(qrCodeService).getUserHistory("user1");
    }

    @Test
    void testGetHistoryWithoutUserIdThrows() {
        when(request.getAttribute("userId")).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            controller.getHistory(request);
        });

        assertEquals(401, exception.getStatusCode().value());
    }
}
