package pl.lotto.domain.numberreceiver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class NumberReceiverConfiguration {

    @Bean
    NumberReceiverFacade createForTest(Clock clock, NumberReceiverRepository repository, TicketIdGenerator idGenerator) {
        NumberValidator numberValidator = new NumberValidator();
        SaturdayDrawDateProvider drawDateProvider = new SaturdayDrawDateProvider(clock);

        return new NumberReceiverFacade(numberValidator, repository, idGenerator, drawDateProvider);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
