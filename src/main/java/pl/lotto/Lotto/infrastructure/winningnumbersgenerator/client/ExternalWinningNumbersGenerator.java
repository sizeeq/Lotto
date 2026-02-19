package pl.lotto.Lotto.infrastructure.winningnumbersgenerator.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import pl.lotto.Lotto.domain.winningnumbersgenerator.WinningNumbersGenerator;
import pl.lotto.Lotto.domain.winningnumbersgenerator.WinningNumbersProperties;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Log4j2
public class ExternalWinningNumbersGenerator implements WinningNumbersGenerator {

    private final RestClient restClient;
    private final WinningNumbersProperties properties;
    private final HttpClientWinningNumbersProperties httpProperties;

    public ExternalWinningNumbersGenerator(RestClient restClient, WinningNumbersProperties properties, HttpClientWinningNumbersProperties httpProperties) {
        this.restClient = restClient;
        this.properties = properties;
        this.httpProperties = httpProperties;
    }

    @Override
    public Set<Integer> generate() {
        log.info("Fetching winning numbers from external service...");

        try {
            List<Integer> numbers = makeGetRequest();
            return parseNumbersFromList(numbers);
        } catch (ResponseStatusException exception) {
            log.error("Error during winning numbers fetch: {} {}", exception.getStatusCode(), exception.getReason());
            throw exception;
        } catch (RestClientResponseException exception) {
            log.error("External service responded with error: {} {}", exception.getStatusCode(), exception.getMessage());
            throw new ResponseStatusException(exception.getStatusCode(), exception.getMessage());
        } catch (Exception exception) {
            log.error("Unexpected error: {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    private List<Integer> makeGetRequest() {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(httpProperties.getRandomNumberServicePath())
                        .queryParam("min", properties.getLowerBound())
                        .queryParam("max", properties.getUpperBound())
                        .queryParam("count", properties.getRequiredNumbers())
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new ResponseStatusException(response.getStatusCode(), "Client error while fetching winning numbers");
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new ResponseStatusException(response.getStatusCode(), "Server error while fetching winning numbers");
                }))
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private Set<Integer> parseNumbersFromList(List<Integer> numbers) {
        if (numbers == null || numbers.size() != properties.getRequiredNumbers()) {
            log.error("Invalid number set fetched: {}", numbers);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }

        log.info("Successfully fetched winning numbers: {}", numbers);
        return Set.copyOf(numbers);
    }
}
