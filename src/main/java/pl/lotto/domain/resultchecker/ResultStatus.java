package pl.lotto.domain.resultchecker;

public enum ResultStatus {

    WIN("You have won!"),
    LOSE("Try again!");

    private final String message;

    ResultStatus(String message) {
        this.message = message;
    }
}
