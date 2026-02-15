package pl.lotto.Lotto.domain.resultannouncer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lotto.Lotto.domain.resultannouncer.dto.ResultAnnouncerResponseDto;
import pl.lotto.Lotto.domain.resultchecker.ResultCheckerFacade;
import pl.lotto.Lotto.domain.resultchecker.ResultStatus;
import pl.lotto.Lotto.domain.resultchecker.dto.ResultDto;
import pl.lotto.Lotto.domain.resultchecker.exception.ResultNotFoundException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultAnnouncerEntityFacadeTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-02-04T12:00:00Z"), ZoneId.systemDefault());
    ResultAnnouncerFacade resultAnnouncerFacade;
    @Mock
    private ResultCheckerFacade resultCheckerFacade;
    @Mock
    private ResultAnnouncerRepository repository;

    @BeforeEach
    public void setUp() {
        resultAnnouncerFacade = new ResultAnnouncerFacade(resultCheckerFacade, repository, clock);
    }

    @Test
    @DisplayName("Should return wait message when checking before announcement time")
    void should_return_wait_message_when_checking_before_announcement_time() {
        //given
        String ticketId = "123";
        LocalDateTime futureDrawDate = LocalDateTime.now(clock).plusMinutes(1);
        ResultDto resultDto = ResultDto.builder()
                .ticketId(ticketId)
                .drawDate(futureDrawDate)
                .status(ResultStatus.WIN)
                .build();

        when(repository.findById(ticketId)).thenReturn(Optional.empty());
        when(resultCheckerFacade.findResultByTicketId(ticketId)).thenReturn(resultDto);

        //when
        ResultAnnouncerResponseDto resultAnnouncerResponseDto = resultAnnouncerFacade.checkResult(ticketId);

        //then
        assertThat(resultAnnouncerResponseDto.message()).isEqualTo("Results are being calculated, please come back later");

    }

    @Test
    @DisplayName("Should return cached result when it exists in repository")
    void should_return_cached_result_when_it_exists_in_repository() {
        //given
        String ticketId = "123";

        ResultAnnouncerEntity resultAnnouncerEntity = ResultAnnouncerEntity.builder().ticketId(ticketId).build();

        when(repository.findById(ticketId)).thenReturn(Optional.of(resultAnnouncerEntity));

        //when
        ResultAnnouncerResponseDto actualResultAnnouncerResponseDto = resultAnnouncerFacade.checkResult(ticketId);

        //then
        verify(resultCheckerFacade, never()).findResultByTicketId(anyString());
        assertThat(actualResultAnnouncerResponseDto.resultDetailsDto().ticketId()).isEqualTo(ticketId);
        assertThat(actualResultAnnouncerResponseDto.message()).isEqualTo("You have already checked your results, come back later");
    }

    @Test
    @DisplayName("Should fetch result from ResultCheckerFacade and save to cache when not found in repository")
    void should_fetch_result_from_result_checker_and_save_to_cache_when_not_found_in_repository() {
        //given
        String ticketId = "123";
        LocalDateTime drawDateInPast = LocalDateTime.of(2026, 2, 4, 12, 0);
        ResultDto checkerResult = ResultDto.builder().ticketId(ticketId).status(ResultStatus.WIN).matchedNumbers(4).drawDate(drawDateInPast).build();

        when(repository.findById(ticketId)).thenReturn(Optional.empty());
        when(resultCheckerFacade.findResultByTicketId(ticketId)).thenReturn(checkerResult);

        //when
        ResultAnnouncerResponseDto resultAnnouncerResponseDto = resultAnnouncerFacade.checkResult(ticketId);

        //then
        verify(repository).save(any(ResultAnnouncerEntity.class));
        assertThat(resultAnnouncerResponseDto.resultDetailsDto().ticketId()).isEqualTo(ticketId);
        assertThat(resultAnnouncerResponseDto.message()).contains("Congratulations, you've won and hit 4 numbers!");
    }

    @Test
    @DisplayName("Should return losing message when player lost")
    void should_return_losing_message_when_player_lost() {
        //given
        String ticketId = "123";
        LocalDateTime drawDateInPast = LocalDateTime.of(2026, 2, 4, 12, 0);
        ResultDto checkerResult = ResultDto.builder().ticketId(ticketId).status(ResultStatus.LOSE).matchedNumbers(2).drawDate(drawDateInPast).build();

        when(repository.findById(ticketId)).thenReturn(Optional.empty());
        when(resultCheckerFacade.findResultByTicketId(ticketId)).thenReturn(checkerResult);

        //when
        ResultAnnouncerResponseDto resultAnnouncerResponseDto = resultAnnouncerFacade.checkResult(ticketId);

        //then
        verify(repository).save(any(ResultAnnouncerEntity.class));
        assertThat(resultAnnouncerResponseDto.resultDetailsDto().ticketId()).isEqualTo(ticketId);
        assertThat(resultAnnouncerResponseDto.resultDetailsDto().matchedNumbers()).isEqualTo(2);
        assertThat(resultAnnouncerResponseDto.message()).contains("Try again!");
    }

    @Test
    @DisplayName("Should throw exception when ticket result was not found in ResultCheckerFacade")
    void should_throw_exception_when_ticket_result_was_not_found_in_result_checker_facade() {
        //given
        String ticketId = "non-existing-id";

        when(repository.findById(ticketId)).thenReturn(Optional.empty());
        when(resultCheckerFacade.findResultByTicketId(ticketId)).thenThrow(new ResultNotFoundException("Result for id: non-existing-id was not found"));

        //when & then
        assertThatThrownBy(() -> resultAnnouncerFacade.checkResult(ticketId)).isInstanceOf(ResultNotFoundException.class).hasMessage("Result for id: non-existing-id was not found");
    }

    @Test
    @DisplayName("Should not save to cache if result already exists")
    void should_not_save_to_cache_if_result_already_exists() {
        //given
        String ticketId = "123";
        ResultAnnouncerEntity cachedResult = ResultAnnouncerEntity.builder().ticketId(ticketId).status(ResultStatus.WIN).build();

        when(repository.findById(ticketId)).thenReturn(Optional.of(cachedResult));

        //when
        resultAnnouncerFacade.checkResult(ticketId);

        //then
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should return correct DTO after saving new announcement")
    void should_return_correct_dto_after_saving_new_announcement() {
        //given
        String ticketId = "123";
        LocalDateTime drawDateInPast = LocalDateTime.of(2026, 2, 4, 12, 0);
        ResultDto resultDto = ResultDto.builder().ticketId(ticketId).status(ResultStatus.WIN).matchedNumbers(5).drawDate(drawDateInPast).build();

        when(repository.findById(ticketId)).thenReturn(Optional.empty());
        when(resultCheckerFacade.findResultByTicketId(ticketId)).thenReturn(resultDto);

        //when
        ResultAnnouncerResponseDto resultAnnouncerResponseDto = resultAnnouncerFacade.checkResult(ticketId);

        //then
        assertThat(resultAnnouncerResponseDto.resultDetailsDto().ticketId()).isEqualTo(ticketId);
        assertThat(resultAnnouncerResponseDto.message()).contains("Congratulations, you've won and hit 5 numbers!");

    }
}