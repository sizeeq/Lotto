package pl.lotto.Lotto.infrastructure.winningnumbersgenerator.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import pl.lotto.Lotto.domain.winningnumbersgenerator.WinningNumbersGenerator;
import pl.lotto.Lotto.domain.winningnumbersgenerator.WinningNumbersProperties;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Log4j2
public class ExternalWinningNumbersGenerator implements WinningNumbersGenerator {

    private final WebClient webClient;
    private final WinningNumbersProperties properties;
    private final HttpClientWinningNumbersProperties httpProperties;

    public ExternalWinningNumbersGenerator(WebClient webClient, WinningNumbersProperties properties, HttpClientWinningNumbersProperties httpProperties) {
        this.webClient = webClient;
        this.properties = properties;
        this.httpProperties = httpProperties;
    }

    @Override
    public Set<Integer> generate() {
        log.info("Fetching winning numbers from external service...");

        try {
            List<Integer> numbers = makeGetRequest();
            return parseNumbersFromList(numbers);
        } catch (WebClientResponseException e) {
            log.error("External service responded with error: {} {}", e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Failed to fetch numbers due to: {}", e.getMessage());
        }

        return Collections.emptySet();
    }

    private Set<Integer> parseNumbersFromList(List<Integer> numbers) {
        if (numbers == null || numbers.size() != properties.getRequiredNumbers()) {
            log.error("Invalid number set fetched: {}", numbers);
            return Collections.emptySet();
        }

        log.info("Successfully fetched numbers: {}", numbers);
        return Set.copyOf(numbers);
    }

    private List<Integer> makeGetRequest() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(httpProperties.getRandomNumberServicePath())
                        .queryParam("min", properties.getLowerBound())
                        .queryParam("max", properties.getUpperBound())
                        .queryParam("count", properties.getRequiredNumbers())
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResponseStatusException(
                                response.statusCode(),
                                "Client error while fetching winning numbers: " + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Server error while fetching winning numbers")))
                .bodyToFlux(Integer.class)
                .collectList()
                .block();
    }
}
