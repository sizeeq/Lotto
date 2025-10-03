package pl.lotto.Lotto.domain.resultchecker;

import pl.lotto.Lotto.domain.numberreceiver.Ticket;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResultChecker {

    private final static int NUMBERS_REQUIRED_TO_WIN = 3;

    public List<Result> calculateResults(List<Ticket> ticketsByDrawDate, Set<Integer> winningNumbers) {
        return ticketsByDrawDate.stream()
                .map(ticket -> {
                    Set<Integer> matchedNumbers = findMatchedNumbers(ticket.numbers(), winningNumbers);
                    ResultStatus resultStatus = generateResultStatus(matchedNumbers);

                    return buildResult(winningNumbers, ticket, resultStatus, matchedNumbers);
                })
                .toList();
    }

    private Result buildResult(Set<Integer> winningNumbers, Ticket ticket, ResultStatus resultStatus, Set<Integer> matchedNumbers) {
        return Result.builder()
                .ticketId(ticket.id())
                .userNumbers(ticket.numbers())
                .winningNumbers(winningNumbers)
                .drawDate(ticket.drawDate())
                .status(resultStatus)
                .matchedNumbers(matchedNumbers.size())
                .build();
    }

    private Set<Integer> findMatchedNumbers(Set<Integer> numbersFromUser, Set<Integer> winningNumbers) {
        return numbersFromUser.stream()
                .filter(winningNumbers::contains)
                .collect(Collectors.toSet());
    }

    private ResultStatus generateResultStatus(Set<Integer> matchedNumbers) {
        return matchedNumbers.size() >= NUMBERS_REQUIRED_TO_WIN ? ResultStatus.WIN : ResultStatus.LOSE;
    }
}
