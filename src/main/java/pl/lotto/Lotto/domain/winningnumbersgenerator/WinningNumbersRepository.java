package pl.lotto.Lotto.domain.winningnumbersgenerator;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WinningNumbersRepository {

    WinningNumbers save(WinningNumbers winningNumbers);

    Optional<WinningNumbers> findByDrawDate(LocalDateTime drawDate);
}
