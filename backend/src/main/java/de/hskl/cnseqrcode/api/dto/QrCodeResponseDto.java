package de.hskl.cnseqrcode.api.dto;

import java.time.Instant;

public record QrCodeResponseDto(
    String id,
    String imageURL,
    Instant createdAt
) {}
