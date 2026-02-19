package pl.lotto.http.winningnumbergenerator;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.lotto.Lotto.domain.winningnumbersgenerator.WinningNumbersGenerator;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ExternalWinningNumbersGeneratorErrorIntegrationTest {

    public static String CONTENT_TYPE_HEADER_KEY = "Content-Type";
    public static String APPLICATION_JSON_CONTENT_TYPE_VALUE = "application/json";

    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    WinningNumbersGenerator winningNumbersGenerator = new WinningNumberGeneratorIntegrationTestConfig()
            .externalWinningNumbersGeneratorClient(wireMockServer.getPort());

    @Test
    void should_throw_internal_server_error_when_fault_connection_reset_by_peer() {
        //given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=25")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(CONTENT_TYPE_HEADER_KEY, APPLICATION_JSON_CONTENT_TYPE_VALUE)
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        //when
        Throwable throwable = catchThrowable(() -> winningNumbersGenerator.generate());

        //then
        assertAll(() -> {
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        });
    }

    @Test
    void should_throw_internal_server_error_when_fault_empty_response() {
        //given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=25")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(CONTENT_TYPE_HEADER_KEY, APPLICATION_JSON_CONTENT_TYPE_VALUE)
                        .withFault(Fault.EMPTY_RESPONSE)));

        //when
        Throwable throwable = catchThrowable(() -> winningNumbersGenerator.generate());

        //then
        assertAll(() -> {
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        });
    }

    @Test
    void should_throw_internal_server_error_when_fault_malformed_response_chunk() {
        //given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=25")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(CONTENT_TYPE_HEADER_KEY, APPLICATION_JSON_CONTENT_TYPE_VALUE)
                        .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        //when
        Throwable throwable = catchThrowable(() -> winningNumbersGenerator.generate());

        //then
        assertAll(() -> {
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        });
    }

    @Test
    void should_throw_internal_server_error_when_fault_random_data_then_close() {
        //given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=25")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(CONTENT_TYPE_HEADER_KEY, APPLICATION_JSON_CONTENT_TYPE_VALUE)
                        .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //when
        Throwable throwable = catchThrowable(() -> winningNumbersGenerator.generate());

        //then
        assertAll(() -> {
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        });
    }

    @Test
    void should_throw_no_content_exception_when_status_not_content() {
        //given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=25")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NO_CONTENT.value())
                        .withHeader(CONTENT_TYPE_HEADER_KEY, APPLICATION_JSON_CONTENT_TYPE_VALUE)
                        .withBody("""
                                [1, 2, 3, 4, 5, 6]
                                """.trim())));

        //when
        Throwable throwable = catchThrowable(() -> winningNumbersGenerator.generate());

        //then
        assertAll(() -> {
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        });
    }

    @Test
    void should_throw_not_found_status_exception_when_http_service_returning_not_found_status() {
        //given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=25")
                .willReturn(WireMock.aResponse()
                        .withHeader(CONTENT_TYPE_HEADER_KEY, APPLICATION_JSON_CONTENT_TYPE_VALUE)
                        .withStatus(HttpStatus.NOT_FOUND.value())));

        //when
        Throwable throwable = catchThrowable(() -> winningNumbersGenerator.generate());

        //then
        assertAll(() -> {
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        });
    }

    @Test
    void should_throw_unauthorized_exception_when_http_service_returning_unauthorized_status() {
        //given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=25")
                .willReturn(WireMock.aResponse()
                        .withHeader(CONTENT_TYPE_HEADER_KEY, APPLICATION_JSON_CONTENT_TYPE_VALUE)
                        .withStatus(HttpStatus.UNAUTHORIZED.value())));

        //when
        Throwable throwable = catchThrowable(() -> winningNumbersGenerator.generate());

        //then
        assertAll(() -> {
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        });
    }

    @Test
    void should_throw_internal_server_error_when_response_delay_is_5000_and_client_has_1000_ms_read_timeout() {
        //given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=25")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(CONTENT_TYPE_HEADER_KEY, APPLICATION_JSON_CONTENT_TYPE_VALUE)
                        .withBody("""
                                [1, 2, 3, 4, 5, 6]
                                """.trim())
                        .withFixedDelay(5000)));

        //when
        Throwable throwable = catchThrowable(() -> winningNumbersGenerator.generate());

        //then
        assertAll(() -> {
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        });
    }
}
