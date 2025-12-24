package pl.rychellos.hotel.lib.exceptions;

import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final ApplicationExceptionFactory applicationExceptionFactory;

    public GlobalExceptionHandler(ApplicationExceptionFactory applicationExceptionFactory) {
        this.applicationExceptionFactory = applicationExceptionFactory;
    }

    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(ApplicationException exception) {
        return exception.getProblemDetail();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        return applicationExceptionFactory.forbidden(exception.getMessage()).getProblemDetail();
    }
}
