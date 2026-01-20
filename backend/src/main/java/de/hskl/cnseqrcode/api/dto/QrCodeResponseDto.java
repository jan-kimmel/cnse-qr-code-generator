package de.hskl.cnseqrcode.api.dto;

public record QrCodeResponseDto(
    String id,
    String text,
    String imageUrl
) {}
