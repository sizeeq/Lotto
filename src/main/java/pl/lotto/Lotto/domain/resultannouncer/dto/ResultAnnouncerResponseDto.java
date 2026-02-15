package pl.lotto.Lotto.domain.resultannouncer.dto;

import lombok.Builder;

@Builder
public record ResultAnnouncerResponseDto(
        ResultDetailsDto resultDetailsDto,
        String message) {
}
