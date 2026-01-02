package de.hskl.cnseqrcode.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.hskl.cnseqrcode.api.dto.QrCodeRequestDto;
import de.hskl.cnseqrcode.api.dto.QrCodeResponseDto;
import de.hskl.cnseqrcode.model.QrCodeEntity;
import de.hskl.cnseqrcode.service.QrBackground;
import de.hskl.cnseqrcode.service.QrCodeService;
import de.hskl.cnseqrcode.service.QrGenerator;
import de.hskl.cnseqrcode.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<QrCodeResponseDto> createQrCode(@Valid @RequestBody QrCodeRequestDto dto, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        QrCodeEntity entity = qrCodeService.create(dto.text(), userId);

        QrCodeResponseDto response = new QrCodeResponseDto(
            entity.getId(),
            ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/qrcodes/{id}.png")
                .buildAndExpand(entity.getId())
                .toUriString(),
            entity.getCreatedAt());

        return ResponseEntity.created(URI.create(response.imageURL())).body(response);
    }

    @GetMapping("/{id}.png")
    public ResponseEntity<Resource> getQrCode(@PathVariable String id) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(storageService.load(id));
    }

    @GetMapping("/{id}/download.png")
    public ResponseEntity<Resource> downloadQrCode(@PathVariable String id) {
        QrCodeEntity entity = qrCodeService.findById(id);
        byte[] png = QrGenerator.generate(entity.getText(), QrBackground.WHITE);

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .header("Content-Disposition", "attachment; filename=\"QR-Code.png\"")
            .body(new ByteArrayResource(png));
    }
    
    @GetMapping("/history")
    public List<QrCodeResponseDto> getHistory(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bitte einloggen");
        }
        
        List<QrCodeResponseDto> qrCodeList = qrCodeService.findAllByUser(userId)
            .stream().map(entity -> new QrCodeResponseDto(
                entity.getId(),
                ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/qrcodes/{id}.png")
                    .buildAndExpand(entity.getId())
                    .toUriString(),
                    entity.getCreatedAt())).toList();
        return qrCodeList;
    }
}
