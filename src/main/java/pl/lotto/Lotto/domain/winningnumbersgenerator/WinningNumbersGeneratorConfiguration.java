package pl.lotto.Lotto.domain.winningnumbersgenerator;

import org.springframework.context.annotation.Configuration;
import pl.lotto.Lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.lotto.Lotto.domain.numberreceiver.NumberValidator;

@Configuration
public class WinningNumbersGeneratorConfiguration {

    NumberGeneratorFacade createForTests(WinningNumbersGenerator winningNumbersGenerator, WinningNumbersRepository repository, NumberReceiverFacade numberReceiverFacade) {
        NumberValidator numberValidator = new NumberValidator();
        return new NumberGeneratorFacade(winningNumbersGenerator, repository, numberValidator, numberReceiverFacade);
    }
}
