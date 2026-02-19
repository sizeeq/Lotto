package pl.lotto.Lotto.infrastructure.winningnumbersgenerator.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lotto.http.client.config")
@Getter
@Setter
public class HttpClientWinningNumbersProperties {

    private String uri;
    private String port;
    private int connectionTimeout;
    private int readTimeout;
    private String randomNumberServicePath;
}
