package pl.lotto.Lotto.infrastructure.resultchecker.scheduler;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.lotto.Lotto.domain.resultchecker.ResultCheckerFacade;
import pl.lotto.Lotto.domain.resultchecker.dto.ResultDto;
import pl.lotto.Lotto.domain.winningnumbersgenerator.WinningNumbersGeneratorFacade;

import java.util.List;

@Component
@Log4j2
public class ResultCheckerScheduler {

    private final ResultCheckerFacade resultCheckerFacade;
    private final WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;

    public ResultCheckerScheduler(ResultCheckerFacade resultCheckerFacade, WinningNumbersGeneratorFacade winningNumbersGeneratorFacade) {
        this.resultCheckerFacade = resultCheckerFacade;
        this.winningNumbersGeneratorFacade = winningNumbersGeneratorFacade;
    }

    @Scheduled(cron = "${lotto.result-checker.config.scheduler-cron}")
    public void calculateWinners() {
        log.info("ResultCheckerScheduler calculating results...");

        if (!winningNumbersGeneratorFacade.areWinningNumbersGeneratedForUpcomingDrawDate()) {
            log.warn("Winning numbers are not generated yet - skipping calculating for now");
            return;
        }

        List<ResultDto> calculateWinners = resultCheckerFacade.calculateWinners();
        log.info("Calculated winners: {}", calculateWinners.size());
    }
}
