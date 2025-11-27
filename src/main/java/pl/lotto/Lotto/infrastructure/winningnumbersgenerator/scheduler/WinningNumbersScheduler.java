package pl.lotto.Lotto.infrastructure.winningnumbersgenerator.scheduler;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.lotto.Lotto.domain.winningnumbersgenerator.NumberGeneratorFacade;
import pl.lotto.Lotto.domain.winningnumbersgenerator.dto.WinningNumbersDto;

@Component
@Log4j2
public class WinningNumbersScheduler {

    private final NumberGeneratorFacade numberGeneratorFacade;

    public WinningNumbersScheduler(NumberGeneratorFacade numberGeneratorFacade) {
        this.numberGeneratorFacade = numberGeneratorFacade;
    }

    @Scheduled(cron = "${lotto.winning-numbers-generator.config.generator-cron}")
    public WinningNumbersDto generateWinningNumbers() {
        log.info("WinningNumbersScheduler generating winning numbers");

        WinningNumbersDto winningNumbersDto = numberGeneratorFacade.generateWinningNumbers();
        log.info("Generated winning numbers: {}", winningNumbersDto.numbers());
        log.info("Draw date: {}", winningNumbersDto.drawDate());

        return winningNumbersDto;
    }
}
