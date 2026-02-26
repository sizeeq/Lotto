package pl.lotto.domain.resultchecker;

import org.springframework.stereotype.Component;
import pl.lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.lotto.domain.numberreceiver.Ticket;
import pl.lotto.domain.numberreceiver.TicketMapper;
import pl.lotto.domain.numberreceiver.dto.TicketDto;
import pl.lotto.domain.resultchecker.dto.ResultDto;
import pl.lotto.domain.resultchecker.exception.ResultNotFoundException;
import pl.lotto.domain.resultchecker.exception.WinningNumbersNotFoundException;
import pl.lotto.domain.winningnumbersgenerator.WinningNumbersGeneratorFacade;
import pl.lotto.domain.winningnumbersgenerator.dto.WinningNumbersDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ResultCheckerFacade {

    private final NumberReceiverFacade numberReceiverFacade;
    private final WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;
    private final ResultChecker resultChecker;
    private final ResultCheckerRepository repository;

    public ResultCheckerFacade(NumberReceiverFacade numberReceiverFacade, WinningNumbersGeneratorFacade winningNumbersGeneratorFacade, ResultChecker resultChecker, ResultCheckerRepository repository) {
        this.numberReceiverFacade = numberReceiverFacade;
        this.winningNumbersGeneratorFacade = winningNumbersGeneratorFacade;
        this.resultChecker = resultChecker;
        this.repository = repository;
    }

    public List<ResultDto> calculateWinners() {
        LocalDateTime nextDrawDate = numberReceiverFacade.getNextDrawDate();

        List<TicketDto> ticketDtos = numberReceiverFacade.getTicketsByDrawDate(nextDrawDate);
        WinningNumbersDto winningNumbersDto = winningNumbersGeneratorFacade.findWinningNumbersByDrawDate(nextDrawDate);

        List<Ticket> tickets = TicketMapper.toEntity(ticketDtos);

        if (winningNumbersDto == null || winningNumbersDto.numbers().isEmpty()) {
            throw new WinningNumbersNotFoundException("Winning numbers are not available yet");
        }

        List<Result> results = resultChecker.calculateResults(tickets, winningNumbersDto.numbers());
        repository.saveAll(results);

        return ResultMapper.toDto(results);
    }

    public ResultDto findResultByTicketId(String id) {
        Result resultById = repository.findById(id)
                .orElseThrow(() -> new ResultNotFoundException("Result for id: " + id + " was not found"));

        return ResultMapper.toDto(resultById);
    }
}
