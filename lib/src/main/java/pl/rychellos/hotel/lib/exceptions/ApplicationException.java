package pl.rychellos.hotel.lib.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ApplicationException extends RuntimeException {
    @Getter
    private final String title;

    @Getter
    private final HttpStatus status;

    @Getter
    private final String detail;

    public ApplicationException(String title, String detail, HttpStatus status) {
        super(detail);
        this.title = title;
        this.status = status;
        this.detail = detail;
    }

    public ProblemDetail getProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, this.detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}