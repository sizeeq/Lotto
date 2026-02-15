package pl.lotto.Lotto.infrastructure.resultannouncer.controller.error;

import org.springframework.http.HttpStatus;

public record ResultControllerErrorResponse(
        String message,
        HttpStatus httpStatus
) {
}
