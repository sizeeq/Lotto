package pl.lotto.Lotto.domain.numberreceiver.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record TicketDto(
        String id,
        Set<Integer> numbers,
        LocalDateTime drawDate
) {
}
