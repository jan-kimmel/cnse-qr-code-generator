package de.hskl.cnseqrcode.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.hskl.cnseqrcode.api.dto.QrCodeRequestDto;
import de.hskl.cnseqrcode.api.dto.QrCodeResponseDto;
import de.hskl.cnseqrcode.api.dto.UserHistoryDto;
import de.hskl.cnseqrcode.service.QrCodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/qr-codes")
public class QrCodeController {
    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @PostMapping
    public ResponseEntity<QrCodeResponseDto> createQrCode(@Valid @RequestBody QrCodeRequestDto dto, HttpServletRequest request) {
        return ResponseEntity.ok(qrCodeService.generate(dto.text(), (String) request.getAttribute("userId")));
    }

    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getQrCode(@PathVariable String id) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeService.loadQrCodeImage(id));
    }

    @GetMapping(value = "/{id}/download", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> downloadQrCode(@PathVariable String id) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
            .header(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename=\"qr-code.png\"")
            .body(qrCodeService.loadQrCodeImage(id));
    }
    
    @GetMapping("/history")
    public ResponseEntity<UserHistoryDto> getHistory(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bitte einloggen");
        }
        return ResponseEntity.ok(qrCodeService.getUserHistory(userId));
    }
}
