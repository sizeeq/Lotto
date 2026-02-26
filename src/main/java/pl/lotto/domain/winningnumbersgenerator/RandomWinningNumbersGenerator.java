package pl.lotto.domain.winningnumbersgenerator;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class RandomWinningNumbersGenerator implements WinningNumbersGenerator {

    private final Random random = new SecureRandom();
    private WinningNumbersProperties properties;

    private final int LOWER_BOUND = properties.getLowerBound();
    private final int UPPER_BOUND = properties.getUpperBound();
    private final int REQUIRED_NUMBERS = properties.getRequiredNumbers();

    public RandomWinningNumbersGenerator(WinningNumbersProperties properties) {
        this.properties = properties;
    }

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
