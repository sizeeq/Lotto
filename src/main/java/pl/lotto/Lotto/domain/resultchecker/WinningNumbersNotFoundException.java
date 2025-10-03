package pl.lotto.Lotto.domain.resultchecker;

public class WinningNumbersNotFoundException extends RuntimeException {
    public WinningNumbersNotFoundException(String message) {
        super(message);
    }
}
