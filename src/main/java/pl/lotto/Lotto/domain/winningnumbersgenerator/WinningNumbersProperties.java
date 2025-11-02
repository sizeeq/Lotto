package pl.lotto.Lotto.domain.winningnumbersgenerator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lotto.winning-numbers-generator.config")
@Getter
@Setter
public class WinningNumbersProperties {

    private int lowerBound;
    private int upperBound;
    private int requiredNumbers;
}
