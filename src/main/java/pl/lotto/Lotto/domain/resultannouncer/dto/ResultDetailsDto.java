package pl.lotto.Lotto.domain.resultannouncer.dto;

import lombok.Builder;
import pl.lotto.Lotto.domain.resultchecker.ResultStatus;

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
