package pl.lotto.domain.resultannouncer;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.lotto.domain.resultchecker.ResultStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Document
public record ResultAnnouncerEntity(
        @Id
        String ticketId,
        Set<Integer> userNumbers,
        Set<Integer> winningNumbers,
        int matchedNumbers,
        LocalDateTime drawDate,
        ResultStatus status
) {
}
