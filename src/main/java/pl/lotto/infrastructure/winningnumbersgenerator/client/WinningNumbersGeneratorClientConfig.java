package pl.lotto.infrastructure.winningnumbersgenerator.client;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import pl.lotto.domain.winningnumbersgenerator.WinningNumbersGenerator;
import pl.lotto.domain.winningnumbersgenerator.WinningNumbersProperties;

@Configuration
public class WinningNumbersGeneratorClientConfig {

    @Bean
    public RestClient winningNumbersRestClient(HttpClientWinningNumbersProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectionTimeout());
        factory.setReadTimeout(properties.getReadTimeout());

        String baseUrl = getBaseUrl(properties);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    @Bean
    public WinningNumbersGenerator externalWinningNumbersGenerator(RestClient restClient, WinningNumbersProperties properties, HttpClientWinningNumbersProperties httpProperties) {
        return new ExternalWinningNumbersGenerator(restClient, properties, httpProperties);
    }

    private static @NotNull String getBaseUrl(HttpClientWinningNumbersProperties properties) {
        return properties.getUri() + ":" + properties.getPort();
    }
}
