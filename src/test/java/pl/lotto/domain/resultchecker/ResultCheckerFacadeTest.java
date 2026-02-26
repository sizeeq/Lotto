package pl.lotto.domain.resultchecker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.lotto.domain.numberreceiver.Ticket;
import pl.lotto.domain.numberreceiver.TicketMapper;
import pl.lotto.domain.resultchecker.dto.ResultDto;
import pl.lotto.domain.resultchecker.exception.ResultNotFoundException;
import pl.lotto.domain.resultchecker.exception.WinningNumbersNotFoundException;
import pl.lotto.domain.winningnumbersgenerator.WinningNumbers;
import pl.lotto.domain.winningnumbersgenerator.WinningNumbersGeneratorFacade;
import pl.lotto.domain.winningnumbersgenerator.WinningNumbersMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultCheckerFacadeTest {

    private final ResultChecker resultChecker = new ResultChecker();
    @Mock
    private NumberReceiverFacade numberReceiverFacade;
    @Mock
    private WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;
    @Mock
    private ResultCheckerRepository repository;
    @Mock
    private ResultCheckerFacade resultCheckerFacade;

    @BeforeEach
    public void setUp() {
        resultCheckerFacade = new ResultCheckerFacade(numberReceiverFacade, winningNumbersGeneratorFacade, resultChecker, repository);
    }

    @Test
    @DisplayName("Should return results for all tickets when winning numbers exist")
    public void should_return_results_for_all_tickets_when_winning_numbers_exist() {
        //given
        LocalDateTime drawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00
        WinningNumbers winningNumbers = WinningNumbers.builder()
                .drawDate(drawDate)
                .numbers(Set.of(1, 2, 3, 4, 5, 6))
                .build();
        List<Ticket> tickets = List.of(
                Ticket.builder()
                        .id("1")
                        .numbers(Set.of(1, 2, 3, 7, 8, 9))
                        .drawDate(drawDate)
                        .build(),
                Ticket.builder()
                        .id("2")
                        .numbers(Set.of(7, 8, 9, 10, 99, 1))
                        .drawDate(drawDate)
                        .build()
        );

        when(numberReceiverFacade.getNextDrawDate()).thenReturn(drawDate);
        when(numberReceiverFacade.getTicketsByDrawDate(drawDate)).thenReturn(TicketMapper.toDto(tickets));
        when(winningNumbersGeneratorFacade.findWinningNumbersByDrawDate(drawDate)).thenReturn(WinningNumbersMapper.toDto(winningNumbers));

        //when
        List<ResultDto> results = resultCheckerFacade.calculateWinners();

        //then
        ResultDto expectedWinningResult = ResultDto.builder()
                .ticketId("1")
                .userNumbers(Set.of(1, 2, 3, 7, 8, 9))
                .winningNumbers(winningNumbers.numbers())
                .drawDate(drawDate)
                .status(ResultStatus.WIN)
                .matchedNumbers(3)
                .build();

        ResultDto expectedLosingResult = ResultDto.builder()
                .ticketId("2")
                .userNumbers(Set.of(7, 8, 9, 10, 99, 1))
                .winningNumbers(winningNumbers.numbers())
                .drawDate(drawDate)
                .status(ResultStatus.LOSE)
                .matchedNumbers(1)
                .build();

        assertThat(results.size()).isEqualTo(2);
        assertThat(results).containsExactlyInAnyOrder(expectedWinningResult, expectedLosingResult);
        verify(numberReceiverFacade).getNextDrawDate();
        verify(numberReceiverFacade).getTicketsByDrawDate(drawDate);
        verify(winningNumbersGeneratorFacade).findWinningNumbersByDrawDate(drawDate);
    }

    @Test
    @DisplayName("Should mark all tickets as lose when no ticket matches enough numbers")
    public void should_mark_all_tickets_as_LOSE_when_no_ticket_matches_enough_numbers() {
        //given
        LocalDateTime drawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00
        WinningNumbers winningNumbers = WinningNumbers.builder()
                .drawDate(drawDate)
                .numbers(Set.of(1, 2, 3, 4, 5, 6))
                .build();
        List<Ticket> tickets = List.of(
                Ticket.builder()
                        .id("1")
                        .numbers(Set.of(1, 7, 8, 9, 10, 11))
                        .drawDate(drawDate)
                        .build(),
                Ticket.builder()
                        .id("2")
                        .numbers(Set.of(7, 8, 9, 10, 99, 55))
                        .drawDate(drawDate)
                        .build()
        );

        when(numberReceiverFacade.getNextDrawDate()).thenReturn(drawDate);
        when(numberReceiverFacade.getTicketsByDrawDate(drawDate)).thenReturn(TicketMapper.toDto(tickets));
        when(winningNumbersGeneratorFacade.findWinningNumbersByDrawDate(drawDate)).thenReturn(WinningNumbersMapper.toDto(winningNumbers));

        //when
        List<ResultDto> resultDtos = resultCheckerFacade.calculateWinners();

        //then
        assertThat(resultDtos.size()).isEqualTo(2);
        assertThat(resultDtos)
                .extracting(ResultDto::ticketId, ResultDto::status, ResultDto::matchedNumbers)
                .containsExactlyInAnyOrder(
                        tuple("1", ResultStatus.LOSE, 1),
                        tuple("2", ResultStatus.LOSE, 0)
                );
        verify(numberReceiverFacade).getNextDrawDate();
        verify(numberReceiverFacade).getTicketsByDrawDate(drawDate);
        verify(winningNumbersGeneratorFacade).findWinningNumbersByDrawDate(drawDate);
    }

    @Test
    @DisplayName("Should return empty results when there are no tickets for draw date")
    public void should_return_empty_results_when_no_tickets_for_draw_date() {
        //given
        LocalDateTime drawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00
        WinningNumbers winningNumbers = WinningNumbers.builder()
                .drawDate(drawDate)
                .numbers(Set.of(1, 2, 3, 4, 5, 6))
                .build();
        List<Ticket> tickets = Collections.emptyList();

        when(numberReceiverFacade.getNextDrawDate()).thenReturn(drawDate);
        when(numberReceiverFacade.getTicketsByDrawDate(drawDate)).thenReturn(TicketMapper.toDto(tickets));
        when(winningNumbersGeneratorFacade.findWinningNumbersByDrawDate(drawDate)).thenReturn(WinningNumbersMapper.toDto(winningNumbers));

        //when
        List<ResultDto> resultDtos = resultCheckerFacade.calculateWinners();

        //then
        assertThat(resultDtos).isEmpty();
        verify(numberReceiverFacade).getNextDrawDate();
        verify(numberReceiverFacade).getTicketsByDrawDate(drawDate);
        verify(winningNumbersGeneratorFacade).findWinningNumbersByDrawDate(drawDate);
    }

    @Test
    @DisplayName("Should throw an exception when winning numbers were not found")
    public void should_throw_exception_when_winning_numbers_not_found() {
        //given
        LocalDateTime drawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00
        WinningNumbers winningNumbers = WinningNumbers.builder()
                .drawDate(drawDate)
                .numbers(Set.of())
                .build();
        List<Ticket> tickets = List.of(
                Ticket.builder()
                        .id("1")
                        .numbers(Set.of(1, 7, 8, 9, 10, 11))
                        .drawDate(drawDate)
                        .build(),
                Ticket.builder()
                        .id("2")
                        .numbers(Set.of(7, 8, 9, 10, 99, 55))
                        .drawDate(drawDate)
                        .build()
        );

        when(numberReceiverFacade.getNextDrawDate()).thenReturn(drawDate);
        when(numberReceiverFacade.getTicketsByDrawDate(drawDate)).thenReturn(TicketMapper.toDto(tickets));
        when(winningNumbersGeneratorFacade.findWinningNumbersByDrawDate(drawDate)).thenReturn(WinningNumbersMapper.toDto(winningNumbers));

        //when
        //then
        assertThatThrownBy(() -> resultCheckerFacade.calculateWinners())
                .isInstanceOf(WinningNumbersNotFoundException.class)
                .hasMessage("Winning numbers are not available yet");
        verify(numberReceiverFacade).getNextDrawDate();
        verify(numberReceiverFacade).getTicketsByDrawDate(drawDate);
        verify(winningNumbersGeneratorFacade).findWinningNumbersByDrawDate(drawDate);
    }

    @Test
    @DisplayName("Should return result for ticketId when result exists")
    public void should_return_result_for_ticketId_when_result_exists() {
        //given
        String ticketId = "123";
        LocalDateTime drawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00
        Result expectedResult = Result.builder()
                .ticketId(ticketId)
                .userNumbers(Set.of(1, 2, 3, 7, 8, 9))
                .winningNumbers(Set.of(1, 2, 3, 4, 5, 6))
                .drawDate(drawDate)
                .status(ResultStatus.WIN)
                .matchedNumbers(3)
                .build();

        when(repository.findById(ticketId)).thenReturn(Optional.of(expectedResult));

        //when
        ResultDto result = resultCheckerFacade.findResultByTicketId(ticketId);

        //then
        assertThat(result).isEqualTo(ResultMapper.toDto(expectedResult));
    }

    @Test
    @DisplayName("Should throw an exception when result for ticketId were not found")
    public void should_throw_exception_when_result_for_ticketId_not_found() {
        //given
        String ticketId = "non-existing-id";
        when(repository.findById(ticketId)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> resultCheckerFacade.findResultByTicketId(ticketId))
                .isInstanceOf(ResultNotFoundException.class)
                .hasMessage("Result for id: non-existing-id was not found");
    }


}