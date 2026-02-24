package pl.lotto.Lotto.infrastructure.security.error;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TokenControllerErrorHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public TokenErrorResponse handleBadCredentials() {
        return new TokenErrorResponse("Bad Credentials", HttpStatus.UNAUTHORIZED);
    }
}
