package pl.lotto.Lotto.domain;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class FixedClockTestConfig {

    @Bean
    public Clock fixedClock() {
        return Clock.fixed(Instant.parse("2025-09-24T10:00:00Z"), ZoneId.of("UTC"));
    }
}
