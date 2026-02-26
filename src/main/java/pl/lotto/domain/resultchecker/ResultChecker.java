package pl.lotto.domain.resultchecker;

import org.springframework.stereotype.Component;
import pl.lotto.domain.numberreceiver.Ticket;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
class ResultChecker {

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
        Set<Integer> matched = new HashSet<>(numbersFromUser);
        matched.retainAll(winningNumbers);
        return matched;
    }

    private ResultStatus generateResultStatus(Set<Integer> matchedNumbers) {
        return matchedNumbers.size() >= NUMBERS_REQUIRED_TO_WIN ? ResultStatus.WIN : ResultStatus.LOSE;
    }
}
