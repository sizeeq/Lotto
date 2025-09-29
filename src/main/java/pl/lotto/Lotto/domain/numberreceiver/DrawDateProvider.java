package pl.lotto.Lotto.domain.numberreceiver;

import java.time.LocalDateTime;

public interface DrawDateProvider {
    LocalDateTime nextDrawDate();
}
