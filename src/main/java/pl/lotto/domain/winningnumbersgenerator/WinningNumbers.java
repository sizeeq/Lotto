package pl.lotto.domain.winningnumbersgenerator;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Document
public record WinningNumbers(
        @Id
        LocalDateTime drawDate,
        Set<Integer> numbers
) {
}
