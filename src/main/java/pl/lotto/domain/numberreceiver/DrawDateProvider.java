package pl.lotto.domain.numberreceiver;

import java.time.LocalDateTime;

public interface DrawDateProvider {
    LocalDateTime nextDrawDate();
}
