package pl.lotto.cache.redis;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import pl.lotto.BaseIntegrationTest;
import pl.lotto.domain.numberreceiver.dto.NumberReceiverResultDto;
import pl.lotto.domain.resultannouncer.ResultAnnouncerFacade;
import pl.lotto.domain.resultchecker.ResultCheckerFacade;
import pl.lotto.domain.resultchecker.exception.ResultNotFoundException;
import pl.lotto.domain.winningnumbersgenerator.exception.WinningNumbersNotFoundException;
import pl.lotto.domain.winningnumbersgenerator.WinningNumbersGeneratorFacade;
import pl.lotto.infrastructure.security.dto.JwtResponseDto;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RedisResultsCacheIntegrationTest extends BaseIntegrationTest {

    @Container
    private static final GenericContainer<?> REDIS;

    @MockitoSpyBean
    ResultAnnouncerFacade resultAnnouncerFacade;

    @Autowired
    WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;

    @Autowired
    ResultCheckerFacade resultCheckerFacade;

    @Autowired
    CacheManager cacheManager;

    static {
        REDIS = new GenericContainer<>("redis").withExposedPorts(6379);
        REDIS.start();
    }

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.redis.port", () -> REDIS.getFirstMappedPort().toString());
        registry.add("spring.cache.type", () -> "redis");
        registry.add("spring.cache.redis.time-to-live", () -> "PT1S");
    }

    @Test
    @DisplayName("Should save result to cache and then invalidate it when time to live passes")
    public void should_save_result_to_cache_and_then_invalidate_by_time_to_live() throws Exception {
        // step 1: stub zewnętrznego serwisu z numerami

        //given && when && then
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=6")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("[1, 2, 3, 4, 5, 6]")));


        // step 2: rejestracja i token

        //given
        String credentials = """
                {
                "username": "cacheUser",
                "password": "cachePass"
                 }
                """.trim();

        //when && then
        mockMvc.perform(post("/register")
                        .content(credentials)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        MvcResult tokenResult = mockMvc.perform(post("/token")
                        .content(credentials)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jwtToken = objectMapper.readValue(
                        tokenResult.getResponse().getContentAsString(),
                        JwtResponseDto.class)
                .token();


        // step 3: Fetching winning numbers

        //given && when && then
        LocalDateTime drawDate = LocalDateTime.of(2025, 10, 11, 12, 0);
        await().atMost(Duration.ofSeconds(20))
                .until(() -> {
                    try {
                        return winningNumbersGeneratorFacade
                                .findWinningNumbersByDrawDate(drawDate)
                                .numbers().size() == 6;
                    } catch (WinningNumbersNotFoundException e) {
                        return false;
                    }
                });


        //step 4: User created a ticket

        //given && when
        MvcResult inputResult = mockMvc.perform(post("/inputNumbers")
                        .content("""
                                {"inputNumbers": [1,2,3,4,5,6]}
                                """.trim())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String ticketId = objectMapper.readValue(
                inputResult.getResponse().getContentAsString(),
                NumberReceiverResultDto.class
        ).ticket().id();


        // step 5: Advancing time before draw

        //given
        clock.plusDaysAndMinutes(1, 115); // 11.10.2025 11:55

        //when && then
        await().atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofSeconds(1))
                .until(() -> {
                    try {
                        return !resultCheckerFacade
                                .findResultByTicketId(ticketId)
                                .userNumbers().isEmpty();
                    } catch (ResultNotFoundException e) {
                        return false;
                    }
                });


        // step 6: Advancing time after draw

        //given && when && then
        clock.plusDaysAndMinutes(0, 6); // 11.10.2025 12:01


        // step 7: Should save result to cache

        //given && when
        mockMvc.perform(get("/results/" + ticketId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(resultAnnouncerFacade, times(1)).checkResult(ticketId);
        assertThat(cacheManager.getCacheNames()).contains("lotto");


        // step 9: cache should be invalidated

        await()
                .atMost(Duration.ofSeconds(4))
                .pollInterval(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    mockMvc.perform(get("/results/" + ticketId)
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk());

                    verify(resultAnnouncerFacade, atLeast(2)).checkResult(ticketId);
                });
    }
}
