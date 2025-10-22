package pl.lotto.Lotto.domain.resultannouncer;

import pl.lotto.Lotto.domain.resultannouncer.dto.ResultAnnouncerDto;
import pl.lotto.Lotto.domain.resultchecker.ResultCheckerFacade;
import pl.lotto.Lotto.domain.resultchecker.dto.ResultDto;

public class ResultAnnouncerFacade {

    private final ResultCheckerFacade resultCheckerFacade;
    private final ResultAnnouncerRepository repository;

    public ResultAnnouncerFacade(ResultCheckerFacade resultCheckerFacade, ResultAnnouncerRepository repository) {
        this.resultCheckerFacade = resultCheckerFacade;
        this.repository = repository;
    }

    public ResultAnnouncerDto announceResult(String ticketId) {
        return repository.findById(ticketId)
                .map(ResultAnnouncerMapper::toDto)
                .orElseGet(() -> {
                    ResultDto resultByTicketId = resultCheckerFacade.findResultByTicketId(ticketId);
                    String resultMessage = buildMessage(resultByTicketId);

                    ResultAnnouncer announcer = ResultAnnouncer.builder()
                            .ticketId(resultByTicketId.ticketId())
                            .userNumbers(resultByTicketId.userNumbers())
                            .winningNumbers(resultByTicketId.winningNumbers())
                            .drawDate(resultByTicketId.drawDate())
                            .status(resultByTicketId.status())
                            .message(resultMessage)
                            .build();

                    repository.save(announcer);

                    return ResultAnnouncerMapper.toDto(announcer);
                });
    }

    private String buildMessage(ResultDto resultDto) {
        return switch (resultDto.status()) {
            case WIN -> String.format("Congratulations, you've won and hit %d numbers!", resultDto.matchedNumbers());
            case LOSE -> "You didn't win this time. Try again!";
        };
    }
}
