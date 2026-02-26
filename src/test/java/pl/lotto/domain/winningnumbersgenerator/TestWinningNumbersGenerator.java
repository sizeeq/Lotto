package pl.lotto.domain.winningnumbersgenerator;

import java.util.Set;

public class TestWinningNumbersGenerator implements WinningNumbersGenerator {

    Set<Integer> winningNumbers;

    TestWinningNumbersGenerator() {
        this.winningNumbers = Set.of(1, 2, 3, 4, 5, 6);
    }

    TestWinningNumbersGenerator(Set<Integer> winningNumbers) {
        this.winningNumbers = winningNumbers;
    }

    @Override
    public Set<Integer> generate() {
        return winningNumbers;
    }
}
