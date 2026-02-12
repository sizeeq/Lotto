package pl.lotto.Lotto.domain.numberreceiver;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@Component
class SaturdayDrawDateProvider implements DrawDateProvider {

    private static final LocalTime DRAW_TIME = LocalTime.of(12, 0, 0);
    private final Clock clock;

    public SaturdayDrawDateProvider(Clock clock) {
        this.clock = clock;
    }

    @Override
    public LocalDateTime nextDrawDate() {
        LocalDateTime now = LocalDateTime.now(clock);
        if (isSaturdayAndBeforeMidday(now)) {
            return combineWithDrawTime(now);
        }

        LocalDateTime nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));

        return combineWithDrawTime(nextSaturday);
    }

    private LocalDateTime combineWithDrawTime(LocalDateTime date) {
        return LocalDateTime.of(date.toLocalDate(), DRAW_TIME);
    }

    private boolean isSaturdayAndBeforeMidday(LocalDateTime dateTime) {
        return dateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) && dateTime.toLocalTime().isBefore(DRAW_TIME);
    }
}
