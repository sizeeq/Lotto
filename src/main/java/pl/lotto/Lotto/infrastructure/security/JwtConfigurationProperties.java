package pl.lotto.Lotto.infrastructure.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("lotto.jwt.config")
@Getter
@Setter
public class JwtConfigurationProperties {

    String secret;
    String issuer;
    int expirationDays;
}
