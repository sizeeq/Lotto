package pl.lotto.Lotto.domain.resultchecker;

import org.springframework.stereotype.Component;
import pl.lotto.Lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.lotto.Lotto.domain.numberreceiver.Ticket;
import pl.lotto.Lotto.domain.numberreceiver.TicketMapper;
import pl.lotto.Lotto.domain.numberreceiver.dto.TicketDto;
import pl.lotto.Lotto.domain.resultchecker.dto.ResultDto;
import pl.lotto.Lotto.domain.winningnumbersgenerator.NumberGeneratorFacade;
import pl.lotto.Lotto.domain.winningnumbersgenerator.dto.WinningNumbersDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class ResultCheckerFacade {

    private final NumberReceiverFacade numberReceiverFacade;
    private final NumberGeneratorFacade numberGeneratorFacade;
    private final ResultChecker resultChecker;
    private final ResultCheckerRepository repository;

    public ResultCheckerFacade(NumberReceiverFacade numberReceiverFacade, NumberGeneratorFacade numberGeneratorFacade, ResultChecker resultChecker, ResultCheckerRepository repository) {
        this.numberReceiverFacade = numberReceiverFacade;
        this.numberGeneratorFacade = numberGeneratorFacade;
        this.resultChecker = resultChecker;
        this.repository = repository;
    }

    public List<ResultDto> calculateWinners() {
        LocalDateTime nextDrawDate = numberReceiverFacade.getNextDrawDate();
        List<TicketDto> ticketDtos = numberReceiverFacade.getTicketsByDrawDate(nextDrawDate);
        WinningNumbersDto winningNumbersDto = numberGeneratorFacade.findWinningNumbersByDrawDate(nextDrawDate);

        List<Ticket> tickets = TicketMapper.toEntity(ticketDtos);
        Set<Integer> winningNumbers = winningNumbersDto.numbers();

        if (winningNumbers == null || winningNumbers.isEmpty()) {
            throw new WinningNumbersNotFoundException("Winning numbers are not available yet");
        }

        List<Result> results = resultChecker.calculateResults(tickets, winningNumbers);
        repository.saveAll(results);

        return ResultMapper.toDto(results);
    }

    public ResultDto findResultByTicketId(String id) {
        Result resultById = repository.findById(id)
                .orElseThrow(() -> new ResultNotFoundException("Result for id: " + id + " was not found"));

        return ResultMapper.toDto(resultById);
    }
}
