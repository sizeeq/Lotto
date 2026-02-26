package pl.lotto.domain.winningnumbersgenerator;

import org.springframework.stereotype.Component;
import pl.lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.lotto.domain.numberreceiver.NumberValidator;
import pl.lotto.domain.numberreceiver.ValidationError;
import pl.lotto.domain.winningnumbersgenerator.dto.WinningNumbersDto;
import pl.lotto.domain.winningnumbersgenerator.exception.WinningNumbersNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class WinningNumbersGeneratorFacade {

    private final WinningNumbersGenerator winningNumbersGenerator;
    private final WinningNumbersRepository repository;
    private final NumberValidator numberValidator;
    private final NumberReceiverFacade numberReceiverFacade;

    public WinningNumbersGeneratorFacade(WinningNumbersGenerator winningNumbersGenerator, WinningNumbersRepository repository, NumberValidator numberValidator, NumberReceiverFacade numberReceiverFacade) {
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

        WinningNumbers winningNumbers = WinningNumbers.builder()
                .drawDate(nextDrawDate)
                .numbers(generatedWinningNumbers)
                .build();

        WinningNumbers save = repository.save(winningNumbers);
        return WinningNumbersMapper.toDto(save);
    }

    public WinningNumbersDto findWinningNumbersByDrawDate(LocalDateTime drawDate) {
        WinningNumbers winningNumbersByDrawDate = repository.findByDrawDate(drawDate).orElseThrow(
                () -> new WinningNumbersNotFoundException("Winning numbers were not found")
        );

        return WinningNumbersMapper.toDto(winningNumbersByDrawDate);
    }

    public boolean areWinningNumbersGeneratedForUpcomingDrawDate() {
        LocalDateTime nextDrawDate = numberReceiverFacade.getNextDrawDate();
        return repository.existsByDrawDate(nextDrawDate);
    }

}
