package pl.lotto.Lotto.domain.resultchecker;

public enum ResultStatus {

    WIN("You have won!"),
    LOSE("Try again!");

    private final String message;

    ResultStatus(String message) {
        this.message = message;
    }
}
