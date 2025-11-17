package pl.lotto.feature;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import pl.lotto.BaseIntegrationTest;
import pl.lotto.Lotto.domain.winningnumbersgenerator.WinningNumbersGenerator;

public class UserPlayedAndWonIntegrationTest extends BaseIntegrationTest {

    @Autowired
    WinningNumbersGenerator winningNumbersGenerator;

    @Test
    public void f() {
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

        //when
        winningNumbersGenerator.generate();

        //then

        // step 2: On 11.10.2025 12:00 system fetched the winning numbers
        //          for draw date 11.10.2025 12:00 from ExternalWinningNumbersGenerator
        //          through NumberGeneratorFacade and saved them to WinningNumbersRepository.

        // step 3: User sent POST /inputNumbers with numbers (1, 2, 3, 4, 5, 6) at time 08.10.2025 10:00.
        //          System responded with HTTP 200 OK and returned:
        //          {
        //            "message": "SUCCESS",
        //            "ticketId": "sampleTicketId",
        //            "drawDate": "2025-10-11T12:00:00"
        //          }

        // step 4: Time advanced to 11.10.2025 12:01 (1 minute after draw time).
        //          System triggered ResultCheckerFacade to calculate results for all tickets with draw date 11.10.2025 12:00.

        // step 5: ResultCheckerFacade produced a result for ticketId = "sampleTicketId"
        //          with matchedNumbers = 6, status = WIN, and persisted it to ResultCheckerRepository.

        // step 6: Time advanced to 11.10.2025 15:01 (1 minute after official announcement time).
        //          ResultAnnouncerFacade was triggered and prepared/cached announcement messages
        //          for all available results in ResultAnnouncerRepository.

        // step 7: User sent GET /results/sampleTicketId.
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
