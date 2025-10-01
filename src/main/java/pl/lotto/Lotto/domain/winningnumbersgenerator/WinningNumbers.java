package pl.lotto.Lotto.domain.winningnumbersgenerator;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record WinningNumbers(
        Set<Integer> numbers,
        LocalDateTime drawDate
) {
}
