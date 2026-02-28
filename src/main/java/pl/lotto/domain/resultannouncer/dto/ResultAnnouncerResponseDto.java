package pl.lotto.domain.resultannouncer.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record ResultAnnouncerResponseDto(
        ResultDetailsDto resultDetailsDto,
        String message)
implements Serializable {
}
