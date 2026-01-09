package pl.rychellos.hotel.webapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final ApplicationExceptionFactory applicationExceptionFactory;

    public GlobalExceptionHandler(ApplicationExceptionFactory applicationExceptionFactory) {
        this.applicationExceptionFactory = applicationExceptionFactory;
    }

    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(ApplicationException exception) {
        log.info("{}: {}", exception.getTitle(), exception.getDetail());

        return exception.getProblemDetail();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        log.info("Access denied", exception);

        return applicationExceptionFactory.forbidden(exception.getMessage()).getProblemDetail();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException exception) {
        log.error("Unhandled access denied", exception);

        return applicationExceptionFactory.unauthorized(exception.getMessage()).getProblemDetail();
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception exception) {
        log.error("Unhandled error", exception);

        return applicationExceptionFactory.internalServerError(exception.getMessage()).getProblemDetail();
    }
}
