package pl.lotto.Lotto.domain.resultannouncer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lotto.Lotto.domain.resultannouncer.dto.ResultAnnouncerDto;
import pl.lotto.Lotto.domain.resultchecker.ResultCheckerFacade;
import pl.lotto.Lotto.domain.resultchecker.ResultNotFoundException;
import pl.lotto.Lotto.domain.resultchecker.ResultStatus;
import pl.lotto.Lotto.domain.resultchecker.dto.ResultDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultAnnouncerFacadeTest {

    ResultAnnouncerFacade resultAnnouncerFacade;
    @Mock
    private ResultCheckerFacade resultCheckerFacade;
    @Mock
    private ResultAnnouncerRepository repository;

    @BeforeEach
    public void setUp() {
        resultAnnouncerFacade = new ResultAnnouncerFacade(resultCheckerFacade, repository);
    }

    @Test
    @DisplayName("Should return cached result when it exists in repository")
    void should_return_cached_result_when_it_exists_in_repository() {
        //given
        String ticketId = "123";
        ResultAnnouncer cached = ResultAnnouncer.builder()
                .ticketId(ticketId)
                .message("Congratulations, you've won and hit 3 numbers!")
                .build();

        when(repository.findById(ticketId)).thenReturn(Optional.of(cached));

        //when
        ResultAnnouncerDto result = resultAnnouncerFacade.announceResult(ticketId);

        //then
        verify(resultCheckerFacade, never()).findResultByTicketId(anyString());
        assertThat(result.ticketId()).isEqualTo(ticketId);
        assertThat(result.message()).isEqualTo("Congratulations, you've won and hit 3 numbers!");
    }

    @Test
    @DisplayName("Should fetch result from ResultCheckerFacade and save to cache when not found in repository")
    void should_fetch_result_from_result_checker_and_save_to_cache_when_not_found_in_repository() {
        //given
        String ticketId = "123";
        ResultDto checkerResult = ResultDto.builder()
                .ticketId(ticketId)
                .status(ResultStatus.WIN)
                .matchedNumbers(4)
                .build();

        when(repository.findById(ticketId)).thenReturn(Optional.empty());
        when(resultCheckerFacade.findResultByTicketId(ticketId)).thenReturn(checkerResult);

        //when
        ResultAnnouncerDto result = resultAnnouncerFacade.announceResult(ticketId);

        //then
        verify(repository).save(any(ResultAnnouncer.class));
        assertThat(result.ticketId()).isEqualTo(ticketId);
        assertThat(result.message()).contains("Congratulation");
    }

    @Test
    @DisplayName("Should return losing message when player lost")
    void should_return_losing_message_when_player_lost() {
        //given
        String ticketId = "123";
        ResultDto checkerResult = ResultDto.builder()
                .ticketId(ticketId)
                .status(ResultStatus.LOSE)
                .matchedNumbers(2)
                .build();

        when(repository.findById(ticketId)).thenReturn(Optional.empty());
        when(resultCheckerFacade.findResultByTicketId(ticketId)).thenReturn(checkerResult);

        //when
        ResultAnnouncerDto result = resultAnnouncerFacade.announceResult(ticketId);

        //then
        verify(repository).save(any(ResultAnnouncer.class));
        assertThat(result.message()).contains("Try again!");
    }

    @Test
    @DisplayName("Should throw exception when ticket result was not found in ResultCheckerFacade")
    void should_throw_exception_when_ticket_result_was_not_found_in_result_checker_facade() {
        //given
        String ticketId = "non-existing-id";

        when(repository.findById(ticketId)).thenReturn(Optional.empty());
        when(resultCheckerFacade.findResultByTicketId(ticketId))
                .thenThrow(new ResultNotFoundException("Result for id: non-existing-id was not found"));

        //when & then
        assertThatThrownBy(() -> resultAnnouncerFacade.announceResult(ticketId))
                .isInstanceOf(ResultNotFoundException.class)
                .hasMessage("Result for id: non-existing-id was not found");
    }

    @Test
    @DisplayName("Should not save to cache if result already exists")
    void should_not_save_to_cache_if_result_already_exists() {
        //given
        String ticketId = "123";
        ResultAnnouncer cachedResult = ResultAnnouncer.builder()
                .ticketId(ticketId)
                .status(ResultStatus.WIN)
                .message("Congratulations!")
                .build();

        when(repository.findById(ticketId)).thenReturn(Optional.of(cachedResult));

        //when
        resultAnnouncerFacade.announceResult(ticketId);

        //then
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should return correct DTO after saving new announcement")
    void should_return_correct_dto_after_saving_new_announcement() {
        //given
        String ticketId = "123";
        ResultDto resultDto = ResultDto.builder()
                .ticketId(ticketId)
                .status(ResultStatus.WIN)
                .matchedNumbers(5)
                .build();

        when(repository.findById(ticketId)).thenReturn(Optional.empty());
        when(resultCheckerFacade.findResultByTicketId(ticketId)).thenReturn(resultDto);

        //when
        ResultAnnouncerDto result = resultAnnouncerFacade.announceResult(ticketId);

        //then
        assertThat(result.ticketId()).isEqualTo(ticketId);
        assertThat(result.message()).contains("Congratulations");

    }
}