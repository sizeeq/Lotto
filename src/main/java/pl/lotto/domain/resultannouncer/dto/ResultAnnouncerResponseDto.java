package pl.lotto.domain.resultannouncer.dto;

import lombok.Builder;

@Builder
public record ResultAnnouncerResponseDto(
        ResultDetailsDto resultDetailsDto,
        String message) {
}
