package pl.lotto.feature;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pl.lotto.BaseIntegrationTest;
import pl.lotto.Lotto.domain.numberreceiver.dto.NumberReceiverResultDto;
import pl.lotto.Lotto.domain.winningnumbersgenerator.WinningNumbersGeneratorFacade;
import pl.lotto.Lotto.domain.winningnumbersgenerator.dto.WinningNumbersDto;
import pl.lotto.Lotto.domain.winningnumbersgenerator.exception.WinningNumbersNotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
public class UserPlayedAndWonIntegrationTest extends BaseIntegrationTest {

    @Autowired
    WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;

    @Test
    public void f() throws Exception {
        // step 1: External service (ExternalWinningNumbersGenerator) returned winning numbers: (1, 2, 3, 4, 5, 6)
        //          for the upcoming draw date: 11.10.2025 12:00 (Saturday).
        //given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=6")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [1, 2, 3, 4, 5, 6]
                                """.trim()
                        )
                )
        );

        // step 2: On 10.10.2025 10:00 system fetched the winning numbers
        //          for draw date 11.10.2025 12:00 from ExternalWinningNumbersGenerator
        //          through NumberGeneratorFacade and saved them to WinningNumbersRepository.

        //given
        LocalDateTime drawDate = LocalDateTime.of(2025, 10, 11, 12, 0);

        //when && then
        await().atMost(Duration.ofSeconds(20))
                .until(() ->
                        {
                            try {
                                WinningNumbersDto winningNumbersDto = winningNumbersGeneratorFacade.findWinningNumbersByDrawDate(drawDate);
                                return winningNumbersDto.numbers().size() == 6;
                            } catch (WinningNumbersNotFoundException e) {
                                return false;
                            }
                        }
                );

        //then

        // step 3: User sent POST /inputNumbers with numbers (1, 2, 3, 4, 5, 6) at time 10.10.2025 10:00.
        //          System responded with HTTP 200 OK and returned:
        //          {
        //            "success" : true,
        //            "ticket" : {
        //              "id" : "generated_id*",
        //              "numbers" : [ 1, 2, 3, 4, 5, 6 ],
        //              "drawDate" : "2025-10-11T12:00:00"
        //             },
        //             "errors" : [ ]
        //           }

        //given
        //when
        ResultActions resultPostInputNumbers = mockMvc.perform(post("/inputNumbers")
                .content(
                        """
                                {
                                         "inputNumbers": [1,2,3,4,5,6]
                                }
                                """
                ).contentType(MediaType.APPLICATION_JSON)
        );
        //then
        MvcResult mvcResultPostInputNumbers = resultPostInputNumbers.andExpect(status().isOk()).andReturn();
        String jsonResultPostInputNumbers = mvcResultPostInputNumbers.getResponse().getContentAsString();
        NumberReceiverResultDto responseDto = objectMapper.readValue(jsonResultPostInputNumbers, NumberReceiverResultDto.class);

        String id = responseDto.ticket().id();

        assertThat(responseDto.success()).isTrue();
        assertThat(responseDto.ticket().drawDate()).isEqualTo(drawDate);
        assertThat(id).isNotNull();

        // step 4: User sent GET /results/notExistingId.
        //          System returned HTTP 404 NOT_FOUND with payload:
        //          {
        //            "message": "Result not found for id: notExistingId"
        //          }

        //given
        String path = "/results/notExistingId";

        //when
        ResultActions performGetResults = mockMvc.perform(get(path));

        //then
        performGetResults.andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                        "message": "Result for id: notExistingId was not found",
                        "httpStatus": "NOT_FOUND"
                        }
                        """.trim()
                ));

        // step 5: Time advanced to 11.10.2025 12:01 (1 minute after draw time).
        //          System triggered ResultCheckerFacade to calculate results for all tickets with draw date 11.10.2025 12:00.

        // step 6: ResultCheckerFacade produced a result for ticketId = "sampleTicketId"
        //          with matchedNumbers = 6, status = WIN, and persisted it to ResultCheckerRepository.

        // step 7: Time advanced to 11.10.2025 15:01 (1 minute after official announcement time).
        //          ResultAnnouncerFacade was triggered and prepared/cached announcement messages
        //          for all available results in ResultAnnouncerRepository.

        // step 8: User sent GET /results/sampleTicketId.
        //          System returned HTTP 200 OK with payload:
        //          {
        //            "ticketId": "sampleTicketId",
        //            "drawDate": "2025-10-11T12:00:00",
        //            "matchedNumbers": 6,
        //            "status": "WIN",
        //            "message": "🎉 Congratulations! You matched 6 numbers and won!"
        //          }

    }
}
