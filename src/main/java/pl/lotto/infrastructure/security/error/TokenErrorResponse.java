package pl.lotto.infrastructure.security.error;

import org.springframework.http.HttpStatus;

public record TokenErrorResponse(
        String message,
        HttpStatus httpStatus
) {

}
