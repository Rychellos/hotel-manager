package pl.rychellos.hotel.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ApplicationExceptionFactory applicationExceptionFactory;
    @Mock
    private LangUtil langUtil;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleApplicationException_ShouldReturnProblemDetail() {
        // Given
        ApplicationException ex = new ApplicationException("Title", "Detail", HttpStatus.BAD_REQUEST);

        // When
        ProblemDetail result = globalExceptionHandler.handleApplicationException(ex);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Title", result.getTitle());
        assertEquals("Detail", result.getDetail());
    }

    @Test
    void handleException_ShouldReturnProblemDetail() {
        // Given
        Exception ex = new Exception("General error");
        ApplicationException wrappedEx = new ApplicationException("Internal Server Error", "General error",
                HttpStatus.INTERNAL_SERVER_ERROR);

        org.mockito.Mockito.when(applicationExceptionFactory.internalServerError("General error"))
                .thenReturn(wrappedEx);

        // When
        ProblemDetail result = globalExceptionHandler.handleException(ex);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatus());
    }
}
