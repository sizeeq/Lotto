package pl.lotto.Lotto.infrastructure.winningnumbersgenerator.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import pl.lotto.Lotto.domain.winningnumbersgenerator.WinningNumbersGenerator;
import pl.lotto.Lotto.domain.winningnumbersgenerator.WinningNumbersProperties;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WinningNumbersGeneratorClientConfig {

    @Bean
    public WebClient winningNumbersWebClient(HttpClientWinningNumbersProperties properties) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectionTimeout())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(properties.getReadTimeout(), TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(properties.getWriteTimeout(), TimeUnit.MILLISECONDS));
                });

        String baseUrl = properties.getUri() + ":" + properties.getPort();

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public WinningNumbersGenerator externalWinningNumbersGenerator(WebClient webClient, WinningNumbersProperties properties, HttpClientWinningNumbersProperties httpProperties) {
        return new ExternalWinningNumbersGenerator(webClient, properties, httpProperties);
    }
}
