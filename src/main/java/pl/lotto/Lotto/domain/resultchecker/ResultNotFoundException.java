package pl.lotto.Lotto.domain.resultchecker;

public class ResultNotFoundException extends RuntimeException {
    public ResultNotFoundException(String message) {
        super(message);
    }
}
