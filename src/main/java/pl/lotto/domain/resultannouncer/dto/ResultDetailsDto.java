package pl.lotto.domain.resultannouncer.dto;

import lombok.Builder;
import pl.lotto.domain.resultchecker.ResultStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record ResultDetailsDto(
        String ticketId,
        Set<Integer> userNumbers,
        Set<Integer> winningNumbers,
        int matchedNumbers,
        LocalDateTime drawDate,
        ResultStatus status) {
}
