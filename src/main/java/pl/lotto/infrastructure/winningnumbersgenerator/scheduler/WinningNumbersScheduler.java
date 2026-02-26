package pl.lotto.infrastructure.winningnumbersgenerator.scheduler;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.lotto.domain.winningnumbersgenerator.WinningNumbersGeneratorFacade;
import pl.lotto.domain.winningnumbersgenerator.dto.WinningNumbersDto;

@Component
@Log4j2
public class WinningNumbersScheduler {

    private final WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;

    public WinningNumbersScheduler(WinningNumbersGeneratorFacade winningNumbersGeneratorFacade) {
        this.winningNumbersGeneratorFacade = winningNumbersGeneratorFacade;
    }

    @Scheduled(cron = "${lotto.winning-numbers-generator.config.generator-cron}")
    public WinningNumbersDto generateWinningNumbers() {
        log.info("WinningNumbersScheduler generating winning numbers...");

        WinningNumbersDto winningNumbersDto = winningNumbersGeneratorFacade.generateWinningNumbers();

        log.info("Generated winning numbers: {}", winningNumbersDto.numbers());
        log.info("Draw date: {}", winningNumbersDto.drawDate());

        return winningNumbersDto;
    }
}
