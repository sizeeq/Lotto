package pl.lotto.http.winningnumbergenerator;

import org.springframework.web.client.RestClient;
import pl.lotto.domain.winningnumbersgenerator.WinningNumbersGenerator;
import pl.lotto.domain.winningnumbersgenerator.WinningNumbersProperties;
import pl.lotto.infrastructure.winningnumbersgenerator.client.HttpClientWinningNumbersProperties;
import pl.lotto.infrastructure.winningnumbersgenerator.client.WinningNumbersGeneratorClientConfig;

public class WinningNumberGeneratorIntegrationTestConfig extends WinningNumbersGeneratorClientConfig {

    public WinningNumbersGenerator externalWinningNumbersGeneratorClient(int wireMockPort) {
        HttpClientWinningNumbersProperties httpProperties = new HttpClientWinningNumbersProperties();
        httpProperties.setUri("http://localhost");
        httpProperties.setPort(String.valueOf(wireMockPort));
        httpProperties.setRandomNumberServicePath("/api/v1.0/random");
        httpProperties.setReadTimeout(1000);
        httpProperties.setConnectionTimeout(2000);

        WinningNumbersProperties properties = new WinningNumbersProperties();
        properties.setLowerBound(1);
        properties.setUpperBound(99);
        properties.setRequiredNumbers(25);

        RestClient restClient = winningNumbersRestClient(httpProperties);

        return externalWinningNumbersGenerator(restClient, properties, httpProperties);
    }
}
