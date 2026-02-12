package pl.lotto.Lotto.domain.numberreceiver;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class NumberValidator {

    private static final int REQUIRED_NUMBERS_FROM_USER = 6;
    private static final int LOWER_BOUND = 1;
    private static final int UPPER_BOUND = 99;

    public List<ValidationError> validate(Set<Integer> numbersFromUser) {
        List<ValidationError> validationErrors = new ArrayList<>();

        int size = numbersFromUser.size();

        if (size > REQUIRED_NUMBERS_FROM_USER) {
            validationErrors.add(ValidationError.TOO_MANY_NUMBERS);
        } else if (numbersFromUser.size() < REQUIRED_NUMBERS_FROM_USER) {
            validationErrors.add(ValidationError.NOT_ENOUGH_NUMBERS);
        }

        if (!isNumberInRange(numbersFromUser)) {
            validationErrors.add(ValidationError.OUT_OF_RANGE);
        }

        return validationErrors;
    }

    private boolean isNumberInRange(Set<Integer> numbers) {
        return numbers.stream()
                .allMatch(number -> number >= LOWER_BOUND && number <= UPPER_BOUND);
    }
}
