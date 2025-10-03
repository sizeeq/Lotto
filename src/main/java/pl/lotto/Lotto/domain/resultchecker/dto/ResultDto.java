package pl.lotto.Lotto.domain.resultchecker.dto;

import lombok.Builder;
import pl.lotto.Lotto.domain.resultchecker.ResultStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record ResultDto(
        String ticketId,
        Set<Integer> userNumbers,
        Set<Integer> winningNumbers,
        LocalDateTime drawDate,
        ResultStatus status,
        int matchedNumbers
) {
}
