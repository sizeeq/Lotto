package pl.lotto.Lotto.domain.resultchecker;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record Result(
        String ticketId,
        Set<Integer> userNumbers,
        Set<Integer> winningNumbers,
        LocalDateTime drawDate,
        ResultStatus status,
        int matchedNumbers
) {
}
