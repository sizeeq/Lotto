package pl.lotto.Lotto.infrastructure.resultannouncer.controller.error;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.lotto.Lotto.domain.resultchecker.exception.ResultNotFoundException;

@ControllerAdvice
@Log4j2
public class ResultAnnouncerControllerErrorHandler {

    @ExceptionHandler(ResultNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultControllerErrorResponse handleResultNotFound(ResultNotFoundException exception) {
        String exceptionMessage = exception.getMessage();
        log.error(exceptionMessage);
        return new ResultControllerErrorResponse(exceptionMessage, HttpStatus.NOT_FOUND);
    }
}
