package pl.lotto.feature;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import pl.lotto.BaseIntegrationTest;
import pl.lotto.domain.numberreceiver.dto.NumberReceiverResultDto;
import pl.lotto.domain.resultchecker.ResultCheckerFacade;
import pl.lotto.domain.resultchecker.dto.ResultDto;
import pl.lotto.domain.resultchecker.exception.ResultNotFoundException;
import pl.lotto.domain.winningnumbersgenerator.WinningNumbersGeneratorFacade;
import pl.lotto.domain.winningnumbersgenerator.dto.WinningNumbersDto;
import pl.lotto.domain.winningnumbersgenerator.exception.WinningNumbersNotFoundException;
import pl.lotto.infrastructure.security.dto.JwtResponseDto;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Log4j2
public class UserPlayedAndWonIntegrationTest extends BaseIntegrationTest {

    @Autowired
    WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;

    @Autowired
    public ResultCheckerFacade resultCheckerFacade;

    @Test
    public void user_should_be_able_to_play_and_check_win_result() throws Exception {
        //Starting date is: 10.10.2025 10:00 -> 10th October Friday 2025 10:00
        //Next draw date is: 11.10.2025 12:00 -> 11th October Saturday 2025 12:00

        // step 1: User attempts to obtain a token via POST /token (username=someUser, password=somePassword)
        // System returns 401 UNAUTHORIZED

        //given
        String postTokenPath = "/token";
        String postTokenContent = """
                {
                "username": "someUsername",
                "password": "somePassword"
                }
                """.trim();

        //when && then
        mockMvc.perform(post(postTokenPath)
                        .content(postTokenContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Bad Credentials"))
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"));


        // step 2: User calls POST /inputNumbers without a token
        // System returns 403 FORBIDDEN

        //given
        String postInputNumbers = "/inputNumbers";

        //when && then
        mockMvc.perform(post(postInputNumbers)
                        .content(
                                """
                                        {
                                                 "inputNumbers": [1,2,3,4,5,6]
                                        }
                                        """.trim())
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isForbidden());


        // step 3: User registers via POST /register (username=someUsername, password=somePassword)
        // System creates the user (role: USER) and returns 201 CREATED

        //given
        String postRegisterPath = "/register";
        String postRegisterContent = """
                {
                "username": "someUsername",
                "password": "somePassword"
                }
                """.trim();

        //when && then
        mockMvc.perform(post(postRegisterPath)
                        .content(postRegisterContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("someUsername"))
                .andExpect(jsonPath("$.isCreated").value(true));


        // step 4: User logs in via POST /token and receives token JWT=AAAA.BBBB.CCC
        // System returns 200 OK

        //given && when && then
        MvcResult postTokenResult = mockMvc.perform(post(postTokenPath)
                        .content(postTokenContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("someUsername"))
                .andExpect(jsonPath("$.token", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+$")))
                .andReturn();

        String contentAsString = postTokenResult.getResponse().getContentAsString();
        JwtResponseDto jwtResponseDto = objectMapper.readValue(contentAsString, JwtResponseDto.class);
        String jwtToken = jwtResponseDto.token();

        // step 5: External service (ExternalWinningNumbersGenerator) returned winning numbers: (1, 2, 3, 4, 5, 6)
        //          for the upcoming draw date: 11.10.2025 12:00 (Saturday).

        //given && when && then
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


        // step 6: On 10.10.2025 10:00 system fetched the winning numbers
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


        // step 7: User sent POST /inputNumbers with numbers (1, 2, 3, 4, 5, 6) at time 10.10.2025 10:00 with JWTToken.
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
        String postInputNumbersPath = "/inputNumbers";

        //when
        MvcResult resultPostInputNumbers = mockMvc.perform(post(postInputNumbersPath)
                        .content(
                                """
                                        {
                                                 "inputNumbers": [1,2,3,4,5,6]
                                        }
                                        """.trim())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.ticket.id").value(hasLength(36)))
                .andExpect(jsonPath("$.errors.size()").value(0))
                .andReturn();

        //then
        String jsonResultPostInputNumbers = resultPostInputNumbers.getResponse().getContentAsString();
        NumberReceiverResultDto responseDto = objectMapper.readValue(jsonResultPostInputNumbers, NumberReceiverResultDto.class);
        String id = responseDto.ticket().id();


        // step 8: User sent GET /results/notExistingId.
        //          System returned HTTP 404 NOT_FOUND with payload:
        //          {
        //            "message": "Result for id: notExistingId was not found",
        //            "httpStatus": "NOT_FOUND"
        //          }

        //given
        String path = "/results/notExistingId";

        //when && then
        mockMvc.perform(get(path)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Result for id: notExistingId was not found"))
                .andExpect(jsonPath("$.httpStatus").value("NOT_FOUND"));


        // step 9: Time advanced to 11.10.2025 11:55 (5 minutes before draw time).

        //given && when && then
        // Advancing clock to 11.10.2025 11:55 (5 minutes before draw time)
        clock.plusDaysAndMinutes(1, 115);


        // step 10: ResultCheckerFacade calculated results for tickets and for ticket: ticketId = "generatedId*"
        //          with matchedNumbers = 6, status = WIN.

        //given && when && then
        await().atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofSeconds(1))
                .until(() ->
                        {
                            try {
                                ResultDto resultByTicketId = resultCheckerFacade.findResultByTicketId(id);
                                return !resultByTicketId.userNumbers().isEmpty();
                            } catch (ResultNotFoundException exception) {
                                return false;
                            }
                        }
                );


        // step 11: Time advanced to 11.10.2025 12:01 (1 minute after official announcement time).
        //          ResultAnnouncerFacade was triggered and prepared/cached announcement messages
        //          for all available results in ResultAnnouncerRepository.

        //given && when && then
        // Advancing clock to 11.10.2025 12:01 (1 minute after draw time)
        clock.plusDaysAndMinutes(0, 6);


        // step 12: User sent GET /results/generatedTickedId*.
        //          System returned HTTP 200 OK with payload:
        //          {
        //            "ticketId": "generatedTickedId*",
        //            "drawDate": "2025-10-11T12:00:00",
        //            "matchedNumbers": 6,
        //            "status": "WIN",
        //            "message": "Congratulations, you've won and hit 6 numbers!"
        //          }

        //given
        String getResultPath = "/results/" + id;

        //when && then
        mockMvc.perform(get(getResultPath)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Congratulations, you've won and hit 6 numbers!"))
                .andExpect(jsonPath("$.resultDetailsDto.ticketId").value(id))
                .andExpect(jsonPath("$.resultDetailsDto.status").value("WIN"))
                .andExpect(jsonPath("$.resultDetailsDto.matchedNumbers").value(6));
    }
}
