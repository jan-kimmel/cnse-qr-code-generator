package de.hskl.cnseqrcode.api.dto;

import jakarta.validation.constraints.NotBlank;

public record QrCodeRequestDto(
    @NotBlank String text
){}
