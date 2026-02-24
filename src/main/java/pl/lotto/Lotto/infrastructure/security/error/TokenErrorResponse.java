package pl.lotto.Lotto.infrastructure.security.error;

import org.springframework.http.HttpStatus;

public record TokenErrorResponse(
        String message,
        HttpStatus httpStatus
) {

}
