package pl.lotto.domain.numberreceiver;

import lombok.Getter;

@Getter
public enum ValidationError {

    OUT_OF_RANGE("Number is out of range"),
    NOT_ENOUGH_NUMBERS("Not enough numbers provided"),
    TOO_MANY_NUMBERS("Too many numbers provided");

    private final String message;

    ValidationError(String message) {
        this.message = message;
    }

}
