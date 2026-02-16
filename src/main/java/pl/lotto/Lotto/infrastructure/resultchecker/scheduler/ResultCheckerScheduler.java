package pl.lotto.Lotto.infrastructure.resultchecker.scheduler;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.lotto.Lotto.domain.resultchecker.ResultCheckerFacade;
import pl.lotto.Lotto.domain.resultchecker.dto.ResultDto;

import java.util.List;

@Component
@Log4j2
public class ResultCheckerScheduler {

    private final ResultCheckerFacade resultCheckerFacade;

    public ResultCheckerScheduler(ResultCheckerFacade resultCheckerFacade) {
        this.resultCheckerFacade = resultCheckerFacade;
    }

    @Scheduled(cron = "${lotto.result-checker.config.scheduler-cron}")
    public List<ResultDto> calculateWinners() {
        log.info("ResultCheckerScheduler calculating results...");

        List<ResultDto> calculateWinners = resultCheckerFacade.calculateWinners();

        log.info("Calculated winners: {}", calculateWinners.size());

        return calculateWinners;
    }
}
