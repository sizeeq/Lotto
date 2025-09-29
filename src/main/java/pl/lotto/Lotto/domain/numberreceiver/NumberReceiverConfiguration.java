package pl.lotto.Lotto.domain.numberreceiver;

import java.time.Clock;

public class NumberReceiverConfiguration {

    NumberReceiverFacade createForTest(Clock clock, NumberReceiverRepository repository, TicketIdGenerator idGenerator) {
        NumberValidator numberValidator = new NumberValidator();
        SaturdayDrawDateProvider drawDateProvider = new SaturdayDrawDateProvider(clock);
        return new NumberReceiverFacade(numberValidator, repository, idGenerator, drawDateProvider);
    }
}
