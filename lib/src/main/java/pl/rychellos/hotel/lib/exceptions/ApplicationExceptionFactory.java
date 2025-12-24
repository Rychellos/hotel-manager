package pl.rychellos.hotel.lib.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.lib.lang.LangUtil;

@Service
public class ApplicationExceptionFactory {
    private final LangUtil langUtil;

    public ApplicationExceptionFactory(LangUtil langUtil) {
        this.langUtil = langUtil;
    }

    public ApplicationException badRequest(String detail) {
        return new ApplicationException(
                langUtil.getMessage("error.generic.badRequest.title"),
                detail,
                HttpStatus.BAD_REQUEST
        );
    }

    public ApplicationException unauthorized(String detail) {
        return new ApplicationException(
                langUtil.getMessage("error.generic.unauthorized.title"),
                detail,
                HttpStatus.UNAUTHORIZED
        );
    }

    public ApplicationException forbidden(String detail) {
        return new ApplicationException(
                langUtil.getMessage("error.generic.forbidden.title"),
                detail,
                HttpStatus.FORBIDDEN
        );
    }

    public ApplicationException resourceNotFound(String detail) {
        return new ApplicationException(
                langUtil.getMessage("error.generic.notFound.title"),
                detail,
                HttpStatus.NOT_FOUND
        );
    }

    public ApplicationException methodNotAllowed(String detail) {
        return new ApplicationException(
                langUtil.getMessage("error.generic.methodNotAllowed.title"),
                detail,
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    public ApplicationException conflict(String detail) {
        return new ApplicationException(
                langUtil.getMessage("error.generic.conflict.title"),
                detail,
                HttpStatus.CONFLICT
        );
    }

    public ApplicationException internalServerError(String detail) {
        return new ApplicationException(
                langUtil.getMessage("error.internalServerError"),
                detail,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    public ApplicationException invalidPatchError(String detail) {
        return new ApplicationException(
                langUtil.getMessage("error.invalidPatchError"),
                detail,
                HttpStatus.BAD_REQUEST
        );
    }
}
