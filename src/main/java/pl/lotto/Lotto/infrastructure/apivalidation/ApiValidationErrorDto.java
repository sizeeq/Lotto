package pl.lotto.Lotto.infrastructure.apivalidation;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ApiValidationErrorDto(
        List<String> errorMessages,
        HttpStatus httpStatus
) {
}
