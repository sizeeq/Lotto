package pl.lotto.feature;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.lotto.BaseIntegrationTest;
import pl.lotto.Lotto.domain.resultchecker.ResultCheckerFacade;
import pl.lotto.Lotto.domain.winningnumbersgenerator.WinningNumbersGeneratorFacade;

@Log4j2
public class UserPlayedAndLostIntegrationTest extends BaseIntegrationTest {

    @Autowired
    WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;

    @Autowired
    public ResultCheckerFacade resultCheckerFacade;

    @Test
    public void user_should_receive_lose_message_when_they_matched_less_than_three_numbers() throws Exception {
        // Starting date is: 10.10.2025 10:00 (Friday)
        // Next draw date is: 11.10.2025 12:00 (Saturday)

        // step 1: External service returns winning numbers (1, 2, 3, 4, 5, 6)
        // given: wireMock stub for /api/v1.0/random

        // step 2: Wait for system to fetch winning numbers for draw date 11.10.2025 12:00
        // given: drawDate LocalDateTime
        // when & then: awaitility until winningNumbersGeneratorFacade finds numbers

        // step 3: User plays with numbers (10, 11, 12, 13, 14, 15) which are NOT winning numbers
        // given: POST /inputNumbers with losing numbers
        // when: perform post
        // then: expect 200 OK and get ticketId from response

        // step 4: Time advanced to 11.10.2025 12:01 (after the draw)
        // given: clock advanced by 1 day and 121 minutes

        // step 5: System calculates results
        // when: resultCheckerFacade.calculateWinners() is called (manually or via awaitility)

        // step 6: User checks result and receives LOSE status
        // given: GET /results/{ticketId}
        // when: perform get
        // then: expect 200 OK and jsonPath values:
        //       $.resultDetailsDto.status == "LOSE"
        //       $.resultDetailsDto.matchedNumbers == 0
        //       $.message == "No luck this time. You matched 0 numbers."
    }
}
