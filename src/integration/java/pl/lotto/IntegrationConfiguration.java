package pl.lotto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pl.lotto.Lotto.AdjustableClock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Configuration
@Profile("integration")
public class IntegrationConfiguration {

    @Bean
    @Primary
    AdjustableClock clock() {
        LocalDate localDate = LocalDate.of(2025, 10, 10);
        LocalTime localTime = LocalTime.of(10, 0, 0);

        return AdjustableClock.ofLocalDateAndLocalTime(localDate, localTime, ZoneId.systemDefault());
    }
}
