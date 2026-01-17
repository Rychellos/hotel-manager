package pl.rychellos.hotel.lib.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import pl.rychellos.hotel.lib.lang.LangUtil;

@ExtendWith(MockitoExtension.class)
class ApplicationExceptionFactoryTest {
    @Mock
    private LangUtil langUtil;

    @InjectMocks
    private ApplicationExceptionFactory factory;

    private final String testDetail = "Specific error detail";

    @Test
    void badRequest_ShouldReturnCorrectException() {
        mockTitle("error.generic.badRequest.title", "Bad Request");
        ApplicationException ex = factory.badRequest(testDetail);
        assertException(ex, "Bad Request", testDetail, HttpStatus.BAD_REQUEST);
    }

    @Test
    void unauthorized_ShouldReturnCorrectException() {
        mockTitle("error.generic.unauthorized.title", "Unauthorized");
        ApplicationException ex = factory.unauthorized(testDetail);
        assertException(ex, "Unauthorized", testDetail, HttpStatus.UNAUTHORIZED);
    }

    @Test
    void forbidden_ShouldReturnCorrectException() {
        mockTitle("error.generic.forbidden.title", "Forbidden");
        ApplicationException ex = factory.forbidden(testDetail);
        assertException(ex, "Forbidden", testDetail, HttpStatus.FORBIDDEN);
    }

    @Test
    void resourceNotFound_ShouldReturnCorrectException() {
        mockTitle("error.generic.notFound.title", "Not Found");
        ApplicationException ex = factory.resourceNotFound(testDetail);
        assertException(ex, "Not Found", testDetail, HttpStatus.NOT_FOUND);
    }

    @Test
    void methodNotAllowed_ShouldReturnCorrectException() {
        mockTitle("error.generic.methodNotAllowed.title", "Method Not Allowed");
        ApplicationException ex = factory.methodNotAllowed(testDetail);
        assertException(ex, "Method Not Allowed", testDetail, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void conflict_ShouldReturnCorrectException() {
        mockTitle("error.generic.conflict.title", "Conflict");
        ApplicationException ex = factory.conflict(testDetail);
        assertException(ex, "Conflict", testDetail, HttpStatus.CONFLICT);
    }

    @Test
    void internalServerError_ShouldReturnCorrectException() {
        mockTitle("error.generic.internalServerError.title", "Internal Server Error");
        ApplicationException ex = factory.internalServerError(testDetail);
        assertException(ex, "Internal Server Error", testDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void invalidPatchError_ShouldReturnCorrectException() {
        mockTitle("error.generic.invalidPatch.title", "Invalid Patch");
        ApplicationException ex = factory.invalidPatchError(testDetail);
        assertException(ex, "Invalid Patch", testDetail, HttpStatus.BAD_REQUEST);
    }

    private void mockTitle(String key, String translatedTitle) {
        when(langUtil.getMessage(key)).thenReturn(translatedTitle);
    }

    private void assertException(ApplicationException ex, String expectedTitle, String expectedDetail, HttpStatus expectedStatus) {
        assertEquals(expectedTitle, ex.getTitle());
        assertEquals(expectedDetail, ex.getDetail());
        assertEquals(expectedStatus, ex.getStatus());

        // Also verify ProblemDetail mapping
        var problem = ex.getProblemDetail();
        assertEquals(expectedTitle, problem.getTitle());
        assertEquals(expectedStatus.value(), problem.getStatus());
        assertEquals(expectedDetail, problem.getDetail());
    }
}
