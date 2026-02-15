package pl.lotto.Lotto.domain.resultchecker;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Document
record Result(
        @Id
        String ticketId,
        Set<Integer> userNumbers,
        Set<Integer> winningNumbers,
        LocalDateTime drawDate,
        ResultStatus status,
        int matchedNumbers
) {
}
