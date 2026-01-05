package de.hskl.cnseqrcode.api.dto;

import java.util.List;

public record UserHistoryDto(
    List<String> texts
) {}
