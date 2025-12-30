package de.hskl.cnseqrcode.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.hskl.cnseqrcode.api.dto.QrCodeRequestDto;
import de.hskl.cnseqrcode.api.dto.QrCodeResponseDto;
import de.hskl.cnseqrcode.model.QrCodeEntity;
import de.hskl.cnseqrcode.service.QrCodeService;
import de.hskl.cnseqrcode.service.StorageService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/qrcodes")
public class QrCodeController {
    private final QrCodeService qrCodeService;
    private final StorageService storageService;

    public QrCodeController(QrCodeService qrCodeService, StorageService storageService) {
        this.qrCodeService = qrCodeService;
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<QrCodeResponseDto> createQrCode(@Valid @RequestBody QrCodeRequestDto dto) {
        QrCodeEntity entity = qrCodeService.create(dto.text());

        QrCodeResponseDto response = new QrCodeResponseDto(entity.getId(),
            ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/qrcodes/{id}.png")
                .buildAndExpand(entity.getId())
                .toUriString());

        return ResponseEntity.created(URI.create(response.imageURL())).body(response);
    }

    @GetMapping("/{id}.png")
    public ResponseEntity<Resource> getQrCode(@PathVariable String id) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(storageService.load(id));
    }
}
