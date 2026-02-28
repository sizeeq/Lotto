package pl.lotto.domain.resultannouncer;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pl.lotto.domain.resultannouncer.dto.ResultAnnouncerResponseDto;
import pl.lotto.domain.resultchecker.ResultCheckerFacade;
import pl.lotto.domain.resultchecker.dto.ResultDto;

import java.time.Clock;
import java.time.LocalDateTime;

import static pl.lotto.domain.resultannouncer.AnnouncementMessage.*;

@Component
public class ResultAnnouncerFacade {

    private final ResultCheckerFacade resultCheckerFacade;
    private final ResultAnnouncerRepository repository;
    private final Clock clock;

    public ResultAnnouncerFacade(ResultCheckerFacade resultCheckerFacade, ResultAnnouncerRepository repository, Clock clock) {
        this.resultCheckerFacade = resultCheckerFacade;
        this.repository = repository;
        this.clock = clock;
    }

    @Cacheable(cacheNames = "lotto")
    public ResultAnnouncerResponseDto checkResult(String ticketId) {
        return repository.findById(ticketId)
                .map(result -> new ResultAnnouncerResponseDto(ResultAnnouncerMapper.toDto(result), RESULT_ALREADY_CHECKED.message))
                .orElseGet(() -> handleNewResult(ticketId));
    }

    private ResultAnnouncerResponseDto handleNewResult(String ticketId) {
        ResultDto resultDto = resultCheckerFacade.findResultByTicketId(ticketId);

        if (resultDto == null) {
            return new ResultAnnouncerResponseDto(null, ID_DOES_NOT_EXIST.message);
        }

        if (!isAfterAnnouncementTime(resultDto)) {
            return new ResultAnnouncerResponseDto(ResultAnnouncerMapper.toDto(resultDto), RESULT_BEING_CALCULATED.message);
        }

        String message = buildMessage(resultDto);
        ResultAnnouncerEntity resultAnnouncerEntity = ResultAnnouncerMapper.toEntity(resultDto);
        repository.save(resultAnnouncerEntity);

        return new ResultAnnouncerResponseDto(ResultAnnouncerMapper.toDto(resultAnnouncerEntity), message);
    }

    private boolean isAfterAnnouncementTime(ResultDto resultDto) {
        if (resultDto.drawDate() == null) {
            throw new IllegalStateException("Draw date cannot be null");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        return now.isAfter(resultDto.drawDate());
    }

    private String buildMessage(ResultDto resultDto) {
        return switch (resultDto.status()) {
            case WIN -> String.format("Congratulations, you've won and hit %d numbers!", resultDto.matchedNumbers());
            case LOSE -> "You didn't win this time. Try again!";
        };
    }
}
