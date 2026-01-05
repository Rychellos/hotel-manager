package pl.rychellos.hotel.webapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final ApplicationExceptionFactory applicationExceptionFactory;

    public GlobalExceptionHandler(ApplicationExceptionFactory applicationExceptionFactory) {
        this.applicationExceptionFactory = applicationExceptionFactory;
    }

    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(ApplicationException exception) {
        log.error("{}: {}", exception.getTitle(), exception.getMessage());

        return exception.getProblemDetail();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        log.error("Access denied: {}", exception.getMessage());

        return applicationExceptionFactory.forbidden(exception.getMessage()).getProblemDetail();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException exception) {
        log.error("Authentication failed: {}", exception.getMessage());

        return applicationExceptionFactory.unauthorized(exception.getMessage()).getProblemDetail();
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception exception) {
        log.error(Arrays.toString(exception.getStackTrace()));

        return applicationExceptionFactory.internalServerError(exception.getMessage()).getProblemDetail();
    }
}
