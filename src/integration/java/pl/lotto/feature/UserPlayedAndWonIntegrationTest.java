package pl.lotto.feature;

import org.junit.jupiter.api.Test;
import pl.lotto.BaseIntegrationTest;

public class UserPlayedAndWonIntegrationTest extends BaseIntegrationTest {

    @Test
    public void f() {
        // step 1: External service (ExternalWinningNumbersGenerator) returned winning numbers: (1, 2, 3, 4, 5, 6)
        //          for the upcoming draw date: 11.10.2025 12:00 (Saturday).

        // step 2: User sent POST /inputNumbers with numbers (1, 2, 3, 4, 5, 6) at time 08.10.2025 10:00.
        //          System responded with HTTP 200 OK and returned:
        //          {
        //            "message": "SUCCESS",
        //            "ticketId": "sampleTicketId",
        //            "drawDate": "2025-10-11T12:00:00"
        //          }

        // step 3: On 11.10.2025 12:00 system fetched the winning numbers
        //          for draw date 11.10.2025 12:00 from ExternalWinningNumbersGenerator
        //          through NumberGeneratorFacade and saved them to WinningNumbersRepository.

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
