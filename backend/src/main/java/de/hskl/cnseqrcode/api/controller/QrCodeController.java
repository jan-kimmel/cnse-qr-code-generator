package de.hskl.cnseqrcode.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hskl.cnseqrcode.api.dto.QrCodeRequestDto;
import de.hskl.cnseqrcode.api.dto.QrCodeResponseDto;
import de.hskl.cnseqrcode.model.QrCodeEntity;
import de.hskl.cnseqrcode.service.QrCodeService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("api/qrcode")
public class QrCodeController {
    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @PostMapping
    public ResponseEntity<QrCodeResponseDto> create(@RequestBody QrCodeRequestDto dto) {
        QrCodeEntity entity = qrCodeService.create(dto.text());
        QrCodeResponseDto response = new QrCodeResponseDto(entity.getId(), "/qrcodes/" + entity.getId() + ".png");
        return ResponseEntity.created(URI.create(response.imageURL())).body(response);
    }

    @GetMapping("/{id}.png")
    public ResponseEntity<Resource> getQrCode(@PathVariable String id) {
        try {
            Path file = Paths.get("data/qrcodes").resolve(id + ".png");
            if (!Files.exists(file)) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = new UrlResource(file.toUri());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
