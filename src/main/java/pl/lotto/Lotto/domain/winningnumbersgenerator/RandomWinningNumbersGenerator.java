package pl.lotto.Lotto.domain.winningnumbersgenerator;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomWinningNumbersGenerator implements WinningNumbersGenerator {

    private static final int LOWER_BOUND = 1;
    private static final int UPPER_BOUND = 99;
    private static final int REQUIRED_NUMBERS = 6;

    private final Random random = new SecureRandom();

    @Override
    public Set<Integer> generate() {
        Set<Integer> numbers = new HashSet<>();

        while (isSixNumbers(numbers)) {
            int generatedNumber = random.nextInt(UPPER_BOUND - LOWER_BOUND) + LOWER_BOUND;
            numbers.add(generatedNumber);
        }

        return numbers;
    }

    private boolean isSixNumbers(Set<Integer> numbers) {
        return numbers.size() < REQUIRED_NUMBERS;
    }
}
