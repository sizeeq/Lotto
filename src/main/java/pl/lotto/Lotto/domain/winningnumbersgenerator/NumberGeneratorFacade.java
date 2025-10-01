package pl.lotto.Lotto.domain.winningnumbersgenerator;

import pl.lotto.Lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.lotto.Lotto.domain.numberreceiver.NumberValidator;
import pl.lotto.Lotto.domain.numberreceiver.ValidationError;
import pl.lotto.Lotto.domain.winningnumbersgenerator.dto.WinningNumbersDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class NumberGeneratorFacade {

    private final WinningNumbersGenerator winningNumbersGenerator;
    private final WinningNumbersRepository repository;
    private final NumberValidator numberValidator;
    private final NumberReceiverFacade numberReceiverFacade;

    public NumberGeneratorFacade(WinningNumbersGenerator winningNumbersGenerator, WinningNumbersRepository repository, NumberValidator numberValidator, NumberReceiverFacade numberReceiverFacade) {
        this.winningNumbersGenerator = winningNumbersGenerator;
        this.repository = repository;
        this.numberValidator = numberValidator;
        this.numberReceiverFacade = numberReceiverFacade;
    }

    public WinningNumbersDto generateWinningNumbers() {
        LocalDateTime nextDrawDate = numberReceiverFacade.getNextDrawDate();
        Set<Integer> generatedWinningNumbers = winningNumbersGenerator.generate();

        List<ValidationError> validatedNumbers = numberValidator.validate(generatedWinningNumbers);
        if (!validatedNumbers.isEmpty()) {
            throw new IllegalArgumentException("Generated numbers are not valid");
        }

        return WinningNumbersDto.builder()
                .numbers(generatedWinningNumbers)
                .drawDate(nextDrawDate)
                .build();
    }

    public WinningNumbersDto findWinningNumbersByDrawDate(LocalDateTime drawDate) {
        WinningNumbers winningNumbersByDrawDate = repository.findByDrawDate(drawDate).orElseThrow(
                () -> new WinningNumbersNotFoundException("Winning numbers were not found")
        );

        return WinningNumbersMapper.toDto(winningNumbersByDrawDate);
    }


}
